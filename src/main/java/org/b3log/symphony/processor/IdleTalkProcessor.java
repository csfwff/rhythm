package org.b3log.symphony.processor;

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
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

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
     * Messages storaged at memory, it won't access database.
     */
    private static final HashMap<String, JSONObject> messages = new HashMap<>();

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
    }

    /**
     * Shows Idle Talk index.
     *
     * @param context
     */
    public void showIdleTalk(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "home/idle-talk.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * Send an idle talk.
     *
     * @param context
     */
    public void sendIdleTalk(final RequestContext context) {
        // From
        final JSONObject currentUser = Sessions.getUser();
        String fromUserId = currentUser.optString(Keys.OBJECT_ID);
        String fromUserName = currentUser.optString(User.USER_NAME);
        String fromUserAvatar = currentUser.optString(UserExt.USER_AVATAR_URL);
        // To
        final Request request = context.getRequest();
        JSONObject requestJSON = request.getJSON();
        String toUserName = requestJSON.optString(User.USER_NAME);
        JSONObject toUser = userQueryService.getUserByName(toUserName);
        String toUserId = toUser.optString(Keys.OBJECT_ID);
        String toUserAvatar = toUser.optString(UserExt.USER_AVATAR_URL);
        // Content
        String theme = requestJSON.optString("theme");
        String content = requestJSON.optString("content");
        if (theme.isEmpty() || content.isEmpty()) {
            context.renderJSON(StatusCodes.ERR);
            return;
        }
        long time = System.currentTimeMillis();
        context.renderJSON(StatusCodes.SUCC);
    }
}
