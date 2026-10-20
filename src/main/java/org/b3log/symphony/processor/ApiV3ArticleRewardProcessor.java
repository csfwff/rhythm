package org.b3log.symphony.processor;

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3ArticleRewardService;
import org.b3log.symphony.service.ArticleRewardMgmtService;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;

@Singleton
public class ApiV3ArticleRewardProcessor {
    @Inject private ApiV3ArticleRewardService rewards;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var processor = beans.getReference(ApiV3ArticleRewardProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        final var writes = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.get("/api/v3/articles/{articleId}/thanks", processor::thanks, auth::contentRead);
        Dispatcher.get("/api/v3/articles/{articleId}/reward", processor::reward, auth::contentRead);
        Dispatcher.post("/api/v3/articles/{articleId}/thanks", processor::thank, auth::contentWrite, writes::handle);
        Dispatcher.post("/api/v3/articles/{articleId}/reward", processor::purchase, auth::contentWrite, writes::handle);
    }
    public void thanks(final RequestContext context) { read(context, ArticleRewardMgmtService.Kind.THANK); }
    public void reward(final RequestContext context) { read(context, ArticleRewardMgmtService.Kind.REWARD); }
    public void thank(final RequestContext context) { write(context, ArticleRewardMgmtService.Kind.THANK); }
    public void purchase(final RequestContext context) { write(context, ArticleRewardMgmtService.Kind.REWARD); }
    private void read(final RequestContext context, final ArticleRewardMgmtService.Kind kind) {
        ApiV3Requests.respond(context, () -> rewards.read(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId"), kind));
    }
    private void write(final RequestContext context, final ArticleRewardMgmtService.Kind kind) {
        ApiV3Requests.respond(context, () -> {
            ApiV3WriteInputs.body(context, java.util.Set.of(), true);
            return rewards.apply(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId"), kind, ApiV3WriteInputs.idempotencyKey(context));
        });
    }
}
