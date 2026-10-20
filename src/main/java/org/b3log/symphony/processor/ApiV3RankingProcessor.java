package org.b3log.symphony.processor;

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.AnonymousViewCheckMidware;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.service.ApiV3RankingQueryService;
import org.b3log.symphony.util.ApiV3RankingDto;
import org.b3log.symphony.util.ApiV3Requests;

@Singleton
public class ApiV3RankingProcessor {
    @Inject private ApiV3RankingQueryService rankings;
    public static void register() {
        final BeanManager beans = BeanManager.getInstance();
        final ApiV3RankingProcessor processor = beans.getReference(ApiV3RankingProcessor.class);
        final ApiV3AuthMidware auth = beans.getReference(ApiV3AuthMidware.class);
        final AnonymousViewCheckMidware anonymous = beans.getReference(AnonymousViewCheckMidware.class);
        Dispatcher.get("/api/v3/rankings/{type}", processor::ranking, auth::optionalContent, anonymous::handle);
    }
    public void ranking(final RequestContext context) {
        ApiV3Requests.read(context, () -> {
            final String type = ApiV3Requests.text(context.pathVar("type"), 16, true);
            return rankings.ranking(type, ApiV3RankingDto.subType(type, context.param("subType")), ApiV3Requests.page(context), ApiV3Requests.size(context));
        });
    }
}
