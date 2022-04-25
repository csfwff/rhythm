/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import com.google.common.collect.HashMultimap;
import org.apache.logging.log4j.Level;
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
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.util.Ids;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.channel.IdleTalkChannel;
import org.b3log.symphony.processor.channel.UserChannel;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.ChatRepository;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.*;
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
     * Chat repository.
     */
    @Inject
    private ChatRepository chatRepository;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final IdleTalkProcessor idleTalkProcessor = beanManager.getReference(IdleTalkProcessor.class);
        Dispatcher.get("/idle-talk", idleTalkProcessor::showIdleTalk, loginCheck::handle);
        Dispatcher.get("/api/idle-talk", idleTalkProcessor::showIdleTalkApi, loginCheck::handle);
        Dispatcher.post("/idle-talk/send", idleTalkProcessor::sendIdleTalk, loginCheck::handle);
        Dispatcher.get("/idle-talk/revoke", idleTalkProcessor::revoke, loginCheck::handle);
        Dispatcher.get("/idle-talk/seek", idleTalkProcessor::seek, loginCheck::handle);
    }

    /**
     * Save message.
     *
     * @param senderId
     * @param receiverId
     * @param message
     */
    private static void saveMessage(String senderId, String receiverId, JSONObject message) {
        String mapId = null;
        // 写数据库
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final ChatRepository chatRepository = beanManager.getReference(ChatRepository.class);
            final Transaction transaction = chatRepository.beginTransaction();
            JSONObject data = new JSONObject();
            data.put("senderId", senderId);
            data.put("receiverId", receiverId);
            data.put("message", message.toString());
            mapId = chatRepository.add(data);
            transaction.commit();
        } catch (RepositoryException e) {
            LOGGER.log(Level.ERROR, "Unable to save message", e);
        }
        // 发送 WebSocket 通知
        // 先通知接收者来新消息了
        final JSONObject cmd = new JSONObject();
        cmd.put(UserExt.USER_T_ID, receiverId);
        cmd.put(Common.COMMAND, "newIdleChatMessage");
        UserChannel.sendCmd(cmd);
        // 再把发信详情发送给双方
        cmd.put("mapId", mapId);
        JSONObject splitMessage = JSONs.clone(message);
        splitMessage.remove("content");
        cmd.put(Common.COMMAND, splitMessage);
        // 发给接收者
        cmd.put("youAre", "receiver");
        IdleTalkChannel.sendCmd(cmd);
        // 发给发送者
        cmd.put(UserExt.USER_T_ID, senderId);
        cmd.put("youAre", "sender");
        IdleTalkChannel.sendCmd(cmd);
    }

    /**
     * seek a message and remove.
     *
     * @param context
     */
    public void seek(final RequestContext context) {
        JSONObject user = Sessions.getUser();
        try {
            user = ApiProcessor.getUserByKey(context.param("apiKey"));
        } catch (NullPointerException ignored) {
        }
        if (user == null) {
            context.renderJSON(StatusCodes.ERR).renderMsg("无法获取用户信息！");
            return;
        }
        try {
            String mapId = context.param("mapId");
            JSONObject message = chatRepository.getMessageById(mapId);
            String fromUserId = message.optString("fromUserId");
            String toUserId = message.optString("toUserId");
            if (toUserId.equals(user.optString(Keys.OBJECT_ID))) {
                // 渲染消息
                String content = message.optString("content");
                content = Emotions.toAliases(content);
                content = Emotions.convert(content);
                content = Markdowns.toHTML(content);
                content = Markdowns.clean(content, "");
                content = MediaPlayers.renderAudio(content);
                content = MediaPlayers.renderVideo(content);
                // 删除消息
                Transaction transaction = chatRepository.beginTransaction();
                chatRepository.remove(mapId);
                transaction.commit();
                // 让发送者销毁
                final JSONObject cmd = new JSONObject();
                cmd.put(UserExt.USER_T_ID, fromUserId);
                cmd.put(Common.COMMAND, mapId);
                cmd.put("youAre", "destroyIdleChatMessage");
                IdleTalkChannel.sendCmd(cmd);
                context.renderJSON(StatusCodes.SUCC).renderData(content);
            } else {
                context.renderJSON(StatusCodes.ERR).renderMsg("你没有查看该消息的权限！");
            }
        } catch (RepositoryException e) {
            LOGGER.log(Level.ERROR, "Unable to seek chat", e);
        }
    }

    /**
     * Revoke a message.
     *
     * @param context
     */
    public void revoke(final RequestContext context) {
        JSONObject user = Sessions.getUser();
        try {
            user = ApiProcessor.getUserByKey(context.param("apiKey"));
        } catch (NullPointerException ignored) {
        }
        if (user == null) {
            context.renderJSON(StatusCodes.ERR).renderMsg("无法获取用户信息！");
            return;
        }
        try {
            String mapId = context.param("mapId");
            JSONObject message = chatRepository.getMessageById(mapId);
            String fromUserId = message.optString("fromUserId");
            String toUserId = message.optString("toUserId");
            if (fromUserId.equals(user.optString(Keys.OBJECT_ID))) {
                Transaction transaction = chatRepository.beginTransaction();
                chatRepository.remove(mapId);
                transaction.commit();
                // 让发送者销毁
                final JSONObject cmd = new JSONObject();
                cmd.put(UserExt.USER_T_ID, toUserId);
                cmd.put(Common.COMMAND, mapId);
                cmd.put("youAre", "destroyIdleChatMessage");
                IdleTalkChannel.sendCmd(cmd);
                context.renderJSON(StatusCodes.SUCC);
            } else {
                context.renderJSON(StatusCodes.ERR).renderMsg("你没有撤回该消息的权限！");
            }
        } catch (RepositoryException e) {
            LOGGER.log(Level.ERROR, "Unable to revoke", e);
        }
    }

    /**
     * Return true if the user has unread chat message.
     *
     * @param userId
     * @return
     */
    public static boolean hasUnreadChatMessage(String userId) {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final ChatRepository chatRepository = beanManager.getReference(ChatRepository.class);
            List<JSONObject> messages = chatRepository.getMessagesByReceiverId(userId);
            return messages.size() != 0;
        } catch (RepositoryException e) {
            LOGGER.log(Level.ERROR, "Unable to get unread chat messages", e);
            return false;
        }
    }

    /**
     * Shows Idle Talk index.
     *
     * @param context
     */
    public void showIdleTalkApi(final RequestContext context) {
        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        } catch (NullPointerException ignored) {
        }
        final String userId = currentUser.optString(Keys.OBJECT_ID);
        JSONObject responseJSON = new JSONObject();
        try {
            List<JSONObject> meSent = chatRepository.getMessagesBySenderId(userId);
            List<JSONObject> meReceived = chatRepository.getMessagesByReceiverId(userId);
            responseJSON.put("meSent", meSent);
            responseJSON.put("meReceived", meReceived);
        } catch (RepositoryException e) {
            responseJSON.put("meSent", "");
            responseJSON.put("meReceived", "");
            LOGGER.log(Level.ERROR, "Get chats failed", e);
        }

        context.renderJSON(StatusCodes.SUCC).renderData(responseJSON);
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
        try {
            List<JSONObject> meSent = chatRepository.getMessagesBySenderId(userId);
            List<JSONObject> meReceived = chatRepository.getMessagesByReceiverId(userId);
            dataModel.put("meSent", meSent);
            dataModel.put("meReceived", meReceived);
        } catch (RepositoryException e) {
            dataModel.put("meSent", "");
            dataModel.put("meReceived", "");
            LOGGER.log(Level.ERROR, "Get chats failed", e);
        }

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Send an idle talk.
     *
     * @param context
     */
    public synchronized void sendIdleTalk(final RequestContext context) {
        // From
        final JSONObject requestJSONObject = context.requestJSON();
        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
        } catch (NullPointerException ignored) {
        }
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
