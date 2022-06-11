package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.symphony.processor.middleware.ApiCheckMidware;
import org.b3log.symphony.repository.ChatInfoRepository;
import org.b3log.symphony.repository.ChatUnreadRepository;
import org.json.JSONObject;

import java.util.List;

@Singleton
public class ChatProcessor {

    @Inject
    private ChatUnreadRepository chatUnreadRepository;

    @Inject
    private ChatInfoRepository chatInfoRepository;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final ApiCheckMidware apiCheck = beanManager.getReference(ApiCheckMidware.class);

        final ChatProcessor chatProcessor = beanManager.getReference(ChatProcessor.class);
        Dispatcher.get("/chat/has-unread", chatProcessor::hasUnreadChatMessage, apiCheck::handle);
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
