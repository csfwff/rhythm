package org.b3log.symphony.processor;

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.util.Requests;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3ArticleMgmtService;
import org.b3log.symphony.util.ApiV3ArticleInputs;
import org.b3log.symphony.util.ApiV3ArticleEditInputs;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import java.util.Set;

@Singleton
public class ApiV3ArticleWriteProcessor {
    @Inject private ApiV3ArticleMgmtService management;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var processor = beans.getReference(ApiV3ArticleWriteProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        final var writes = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.post("/api/v3/articles", processor::create, auth::contentWrite, writes::handle);
        Dispatcher.put("/api/v3/articles/{articleId}", processor::update, auth::contentWrite, writes::handle);
        Dispatcher.delete("/api/v3/articles/{articleId}", processor::remove, auth::contentWrite, writes::handle);
    }
    public void create(final RequestContext context) {
        ApiV3Requests.respond(context, () -> {
            final var body = ApiV3WriteInputs.body(context, ApiV3ArticleInputs.FIELDS, false);
            final String ua = context.header("User-Agent");
            return management.create(ApiV3Requests.viewer(context), body, ApiV3WriteInputs.idempotencyKey(context),
                    Requests.getRemoteAddr(context.getRequest()), null == ua ? "" : ua);
        });
    }
    public void update(final RequestContext context) {
        ApiV3Requests.respond(context, () -> {
            final var body = ApiV3WriteInputs.body(context, ApiV3ArticleEditInputs.FIELDS, false);
            final String ua = context.header("User-Agent");
            return management.update(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId"), body,
                    ApiV3WriteInputs.idempotencyKey(context), Requests.getRemoteAddr(context.getRequest()), null == ua ? "" : ua);
        });
    }
    public void remove(final RequestContext context) {
        ApiV3Requests.respond(context, () -> {
            ApiV3WriteInputs.body(context, Set.of(), true);
            return management.remove(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId"), ApiV3WriteInputs.idempotencyKey(context));
        });
    }

}
