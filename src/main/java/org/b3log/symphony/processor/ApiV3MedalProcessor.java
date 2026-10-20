package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.AnonymousViewCheckMidware;
import org.b3log.symphony.service.ApiV3MedalQueryService;
import org.b3log.symphony.util.ApiV3Requests;

/** Medal read projections, separate from legacy read methods that remove expired grants. */
@Singleton
public class ApiV3MedalProcessor {
    @Inject private ApiV3MedalQueryService query;
    public static void register() {
        final BeanManager beans = BeanManager.getInstance();
        final ApiV3MedalProcessor p = beans.getReference(ApiV3MedalProcessor.class);
        final ApiV3AuthMidware auth = beans.getReference(ApiV3AuthMidware.class);
        final AnonymousViewCheckMidware anonymous = beans.getReference(AnonymousViewCheckMidware.class);
        Dispatcher.get("/api/v3/me/medals", p::mine, auth::profileRead);
        Dispatcher.get("/api/v3/users/{userName}/medals", p::displayed, auth::optionalProfile, anonymous::handle);
    }
    public void mine(final RequestContext c) {
        ApiV3Requests.read(c, () -> query.mine(ApiV3Requests.viewer(c).optString(Keys.OBJECT_ID), ApiV3Requests.page(c), ApiV3Requests.size(c)));
    }
    public void displayed(final RequestContext c) {
        ApiV3Requests.read(c, () -> query.displayed(ApiV3Requests.text(c.pathVar("userName"), 64, true), ApiV3Requests.page(c), ApiV3Requests.size(c)));
    }
}
