package org.b3log.symphony.processor;

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3ArticleVoteService;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3Votes;
import org.b3log.symphony.util.ApiV3WriteInputs;

@Singleton
public class ApiV3VoteProcessor {
    @Inject private ApiV3ArticleVoteService articles;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var processor = beans.getReference(ApiV3VoteProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        final var writes = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.get("/api/v3/articles/{articleId}/vote", processor::read, auth::contentRead);
        Dispatcher.put("/api/v3/articles/{articleId}/vote", processor::set, auth::contentWrite, writes::handle);
        Dispatcher.delete("/api/v3/articles/{articleId}/vote", processor::clear, auth::contentWrite, writes::handle);
    }
    public void read(final RequestContext context) {
        ApiV3Requests.respond(context, () -> articles.read(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId")));
    }
    public void set(final RequestContext context) {
        ApiV3Requests.respond(context, () -> {
            final var body = ApiV3WriteInputs.body(context, ApiV3Votes.FIELDS, false);
            return articles.set(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId"), ApiV3Votes.choice(body), ApiV3WriteInputs.idempotencyKey(context));
        });
    }
    public void clear(final RequestContext context) {
        ApiV3Requests.respond(context, () -> {
            ApiV3WriteInputs.body(context, java.util.Set.of(), true);
            return articles.set(ApiV3Requests.viewer(context), ApiV3Requests.id(context, "articleId"), -1, ApiV3WriteInputs.idempotencyKey(context));
        });
    }
}
