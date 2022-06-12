package org.b3log.symphony.processor;

import org.apache.commons.lang.RandomStringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.util.Crypts;
import org.b3log.latke.util.Paginator;
import org.b3log.symphony.processor.middleware.ApiCheckMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.ChatInfoRepository;
import org.b3log.symphony.repository.ChatUnreadRepository;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@Singleton
public class ChatProcessor {

    @Inject
    private ChatUnreadRepository chatUnreadRepository;

    @Inject
    private ChatInfoRepository chatInfoRepository;

    @Inject
    private DataModelService dataModelService;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final ApiCheckMidware apiCheck = beanManager.getReference(ApiCheckMidware.class);
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final ChatProcessor chatProcessor = beanManager.getReference(ChatProcessor.class);
        Dispatcher.get("/chat", chatProcessor::showChat, loginCheck::handle);
        Dispatcher.get("/chat/has-unread", chatProcessor::hasUnreadChatMessage, apiCheck::handle);
        Dispatcher.get("/chat/get-list", chatProcessor::getList, apiCheck::handle);
        Dispatcher.get("/chat/get-message", chatProcessor::getMessage, apiCheck::handle);

    }

    public void getMessage(final RequestContext context) {

    }

    public void getList(final RequestContext context) {

    }

    /**
     * Shows Chat index.
     *
     * @param context
     */
    public void showChat(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "chat.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final JSONObject currentUser = Sessions.getUser();
        if (null == currentUser) {
            context.sendError(403);
            return;
        }
        // 放 ApiKey
        final String userId = currentUser.optString(Keys.OBJECT_ID);
        final String userPassword = currentUser.optString(User.USER_PASSWORD);
        final String userName = currentUser.optString(User.USER_NAME);
        final JSONObject cookieJSONObject = new JSONObject();
        cookieJSONObject.put(Keys.OBJECT_ID, userId);
        final String random = RandomStringUtils.randomAlphanumeric(16);
        cookieJSONObject.put(Keys.TOKEN, userPassword + ApiProcessor.COOKIE_ITEM_SEPARATOR + random);
        final String key = Crypts.encryptByAES(cookieJSONObject.toString(), Symphonys.COOKIE_SECRET);
        if (null != ApiProcessor.keys.get(userName)) {
            ApiProcessor.removeKeyByUsername(userName);
        }
        ApiProcessor.keys.put(key, currentUser);
        ApiProcessor.keys.put(userName, new JSONObject().put("key", key));
        dataModel.put("apiKey", key);

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    public void hasUnreadChatMessage(RequestContext context) {
        context.renderJSON(new JSONObject().put("result", 0));
        JSONObject currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        String userId = currentUser.optString(Keys.OBJECT_ID);

        Query query = new Query().setFilter(new PropertyFilter("fromId", FilterOperator.EQUAL, userId));
        try {
            List<JSONObject> result = chatUnreadRepository.getList(query);
            if (result != null) {
                context.renderJSON(new JSONObject().put("result", result.size()));
            }
        } catch (RepositoryException ignored) {
        }
    }
}
