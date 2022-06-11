package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.ChatUnreadRepository;
import org.b3log.symphony.util.Sessions;
import org.json.JSONObject;

@Singleton
public class ChatProcessor {

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final ChatProcessor chatProcessor = beanManager.getReference(ChatProcessor.class);
        Dispatcher.get("/chat/hasUnread", chatProcessor::hasUnreadChatMessage);
    }

    public boolean hasUnreadChatMessage(RequestContext context) {
        JSONObject currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));

        String userId = currentUser.optString(Keys.OBJECT_ID);

        final BeanManager beanManager = BeanManager.getInstance();
        final ChatUnreadRepository chatUnreadRepository = beanManager.getReference(ChatUnreadRepository.class);

        Query query = new Query().setFilter(new PropertyFilter("fromId", FilterOperator.EQUAL, userId));
        try {
            JSONObject result = chatUnreadRepository.getFirst(query);
            if (result != null) {
                return true;
            }
        } catch (RepositoryException e) {
            return false;
        }
        return false;
    }
}
