package org.b3log.symphony.processor;

import com.google.common.collect.HashMultimap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.util.Ids;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.channel.UserChannel;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

import java.util.*;

/**
 * 《龙门阵处理器》
 *  由于龙门阵的特殊需求，仅在内存中保存聊天记录，故不连接 Service.
 */
@Singleton
public class IdleTalkProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(IdleTalkProcessor.class);

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Pointtransfer management service.
     */
    @Inject
    private PointtransferMgmtService pointtransferMgmtService;

    /**
     * Messages saved at memory, it won't access database.
     *
     * <MapId, message JSON>
     */
    private static final HashMap<String, JSONObject> messages = new HashMap<>();

    /**
     * Context table map.
     *
     * SenderContext: <SenderId, MapId>
     * ReceiverContext: <ReceiverId, MapId>
     */
    private static final HashMultimap<String, String> senderContext = HashMultimap.create();
    private static final HashMultimap<String, String> receiverContext = HashMultimap.create();

    private static List<JSONObject> getMessagesBySenderId(String senderId) {
        List<JSONObject> message = new ArrayList<>();
        Set<String> sender = senderContext.get(senderId);
        for (String s : sender) {
            message.add(messages.get(s).put("mapId", s));
        }
        return message;
    }

    private static List<JSONObject> getMessagesByReceiverId(String receiverId) {
        List<JSONObject> message = new ArrayList<>();
        Set<String> receiver = receiverContext.get(receiverId);
        for (String r : receiver) {
            message.add(messages.get(r).put("mapId", r));
        }
        return message;
    }

    private static void saveMessage(String senderId, String receiverId, JSONObject message) {
        String mapId = Ids.genTimeMillisId();
        messages.put(mapId, message);
        senderContext.put(senderId, mapId);
        receiverContext.put(receiverId, mapId);
        // 发送 WebSocket 通知
        message.remove("content");
        // 先通知接收者来新消息了
        final JSONObject cmd = new JSONObject();
        cmd.put(UserExt.USER_T_ID, receiverId);
        cmd.put(Common.COMMAND, "newIdleChatMessage");
        UserChannel.sendCmd(cmd);
    }

    private static void removeMessage(String mapId, String senderId, String receiverId) {
        messages.remove(mapId);
        senderContext.remove(senderId, mapId);
        receiverContext.remove(receiverId, mapId);
    }

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);

        final IdleTalkProcessor idleTalkProcessor = beanManager.getReference(IdleTalkProcessor.class);
        Dispatcher.get("/idle-talk", idleTalkProcessor::showIdleTalk, loginCheck::handle, csrfMidware::fill);
        Dispatcher.post("/idle-talk/send", idleTalkProcessor::sendIdleTalk, loginCheck::handle, csrfMidware::check);
        Dispatcher.get("/idle-talk/revoke", idleTalkProcessor::revoke, loginCheck::handle, csrfMidware::check);
    }

    /**
     * Revoke a message.
     *
     * @param context
     */
    public void revoke(final RequestContext context) {
        JSONObject user = Sessions.getUser();
        if (user == null) {
            context.renderJSON(StatusCodes.ERR).renderMsg("无法获取用户信息！");
            return;
        }
        String mapId = context.param("mapId");
        JSONObject message = messages.get(mapId);
        String fromUserId = message.optString("fromUserId");
        String toUserId = message.optString("toUserId");
        if (fromUserId.equals(user.optString(Keys.OBJECT_ID))) {
            messages.remove(mapId);
            senderContext.remove(fromUserId, mapId);
            receiverContext.remove(toUserId, mapId);
            context.renderJSON(StatusCodes.SUCC);
        } else {
            context.renderJSON(StatusCodes.ERR).renderMsg("你没有撤回该消息的权限！");
        }
    }

    /**
     * Return true if the user has unread chat message.
     *
     * @param userId
     * @return
     */
    public static boolean hasUnreadChatMessage(String userId) {
        return receiverContext.get(userId).size() != 0;
    }

    /**
     * Shows Idle Talk index.
     *
     * @param context
     */
    public void showIdleTalk(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "home/idle-talk.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final JSONObject currentUser = Sessions.getUser();
        final String userId = currentUser.optString(Keys.OBJECT_ID);
        List<JSONObject> meSent = getMessagesBySenderId(userId);
        List<JSONObject> meReceived = getMessagesByReceiverId(userId);
        dataModel.put("meSent", meSent);
        dataModel.put("meReceived", meReceived);

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Send an idle talk.
     *
     * @param context
     */
    public synchronized void sendIdleTalk(final RequestContext context) {
        // From
        final JSONObject currentUser = Sessions.getUser();
        String fromUserId = currentUser.optString(Keys.OBJECT_ID);
        String fromUserName = currentUser.optString(User.USER_NAME);
        String fromUserAvatar = currentUser.optString(UserExt.USER_AVATAR_URL);
        // Have balance
        final int balance = currentUser.optInt(UserExt.USER_POINT);
        if (balance - Pointtransfer.TRANSFER_SUM_C_BLMZ < 0) {
            context.renderJSON(StatusCodes.ERR).renderMsg("积分余额不足！");
            return;
        }
        final boolean succ = null != pointtransferMgmtService.transfer(fromUserId, Pointtransfer.ID_C_SYS,
                Pointtransfer.TRANSFER_TYPE_C_BLMZ,
                Pointtransfer.TRANSFER_SUM_C_BLMZ, "", System.currentTimeMillis(), "摆了个龙门阵");
        if (!succ) {
            context.renderJSON(StatusCodes.ERR).renderMsg("积分扣除失败！");
            return;
        }
        // To
        final Request request = context.getRequest();
        JSONObject requestJSON = request.getJSON();
        String toUserName = requestJSON.optString(User.USER_NAME);
        JSONObject toUser = userQueryService.getUserByName(toUserName);
        String toUserId;
        try {
            toUserId = toUser.optString(Keys.OBJECT_ID);
        } catch (Exception e) {
            context.renderJSON(StatusCodes.ERR).renderMsg("用户不存在！");
            return;
        }
        if (fromUserId.equals(toUserId)) {
            context.renderJSON(StatusCodes.ERR).renderMsg("你是怎么想到给自己发信的？");
            return;
        }
        String toUserAvatar = toUser.optString(UserExt.USER_AVATAR_URL);
        // Content
        String theme = requestJSON.optString("theme");
        String content = requestJSON.optString("content");
        if (theme.isEmpty() || content.equals("\n")) {
            context.renderJSON(StatusCodes.ERR).renderMsg("主题和正文不得为空！");
            return;
        }
        if (theme.length() > 50) {
            context.renderJSON(StatusCodes.ERR).renderMsg("主题长度不得大于50个字节！");
            return;
        }
        if (content.length() > 20480) {
            context.renderJSON(StatusCodes.ERR).renderMsg("正文长度不得大于20480个字节！");
            return;
        }
        // Storage
        JSONObject message = new JSONObject();
        message.put("fromUserId", fromUserId)
                .put("fromUserName", fromUserName)
                .put("fromUserAvatar", fromUserAvatar);
        message.put("toUserId", toUserId)
                .put("toUserName", toUserName)
                .put("toUserAvatar", toUserAvatar);
        message.put("theme", theme);
        message.put("content", content);
        saveMessage(fromUserId, toUserId, message);

        context.renderJSON(StatusCodes.SUCC);
    }
}
