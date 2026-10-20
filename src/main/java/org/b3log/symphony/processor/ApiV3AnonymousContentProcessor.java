package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.service.ApiV3AnonymousContentQueryService;
import org.b3log.symphony.util.ApiV3Requests;

/** An authenticated owner's anonymous creations are not another user's profile resources. */
@Singleton
public class ApiV3AnonymousContentProcessor {
    @Inject private ApiV3AnonymousContentQueryService query;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var p = beans.getReference(ApiV3AnonymousContentProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        Dispatcher.get("/api/v3/me/anonymous-articles", p::articles, auth::contentRead);
        Dispatcher.get("/api/v3/me/anonymous-comments", p::comments, auth::contentRead);
    }
    public void articles(final RequestContext context) {
        ApiV3Requests.read(context, () -> query.articles(ApiV3Requests.viewer(context).optString(Keys.OBJECT_ID),
                ApiV3Requests.page(context), ApiV3Requests.size(context)));
    }
    public void comments(final RequestContext context) {
        ApiV3Requests.read(context, () -> query.comments(ApiV3Requests.viewer(context).optString(Keys.OBJECT_ID),
                ApiV3Requests.page(context), ApiV3Requests.size(context)));
    }
}
