package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.service.ApiV3ArticleHeatQueryService;
import org.b3log.symphony.util.ApiV3Requests;

@Singleton
public class ApiV3ArticleHeatProcessor {
    @Inject private ApiV3ArticleHeatQueryService query;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var p = beans.getReference(ApiV3ArticleHeatProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        Dispatcher.get("/api/v3/articles/{articleId}/heat", p::heat, auth::contentRead);
    }
    public void heat(final RequestContext context) {
        ApiV3Requests.read(context, () -> query.read(ApiV3Requests.viewer(context).getString(Keys.OBJECT_ID), ApiV3Requests.id(context, "articleId")));
    }
}
