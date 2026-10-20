package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3ProfessionPrimaryService;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import java.util.Set;

/** Explicit Bearer selection; does not enable skip or privacy changes. */
@Singleton
public class ApiV3ProfessionPrimaryProcessor {
    @Inject private ApiV3ProfessionPrimaryService service;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var p = beans.getReference(ApiV3ProfessionPrimaryProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        final var writes = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.put("/api/v3/me/professions/primary", p::set, auth::settingsWrite, writes::handle);
    }
    public void set(final RequestContext c) {
        ApiV3Requests.respond(c, () -> {
            final var body = ApiV3WriteInputs.body(c, Set.of("professionId"), false);
            return service.set(ApiV3Requests.viewer(c).getString(Keys.OBJECT_ID),
                    ApiV3WriteInputs.string(body, "professionId", 19, false), ApiV3WriteInputs.idempotencyKey(c));
        });
    }
}
