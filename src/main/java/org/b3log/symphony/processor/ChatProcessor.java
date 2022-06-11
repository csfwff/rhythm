package org.b3log.symphony.processor;

import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;

import javax.management.Query;

@Singleton
public class ChatProcessor {

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final ChatProcessor chatProcessor = beanManager.getReference(ChatProcessor.class);

    }

    public static boolean hasUnreadChatMessage(String userId) {
        Query query = new Query();

        return false;
    }
}
