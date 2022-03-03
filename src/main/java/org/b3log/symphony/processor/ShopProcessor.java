package org.b3log.symphony.processor;

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.util.StatusCodes;

@Singleton
public class ShopProcessor {

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final ShopProcessor shopProcessor = beanManager.getReference(ShopProcessor.class);
        Dispatcher.get("/buy", shopProcessor::buy, loginCheck::handle);
    }

    public void buy(RequestContext context) {
        context.renderJSON(StatusCodes.ERR).renderMsg("系统商店正在装修中，敬请期待～");
    }

}
