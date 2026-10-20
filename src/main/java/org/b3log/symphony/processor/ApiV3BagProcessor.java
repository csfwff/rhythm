package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3BagMgmtService;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import java.util.Set;

@Singleton
public class ApiV3BagProcessor {
    @Inject private ApiV3BagMgmtService management;
    public static void register() {
        final BeanManager beans = BeanManager.getInstance();
        final ApiV3BagProcessor p = beans.getReference(ApiV3BagProcessor.class);
        final ApiV3AuthMidware auth = beans.getReference(ApiV3AuthMidware.class);
        final ApiV3WriteMidware writes = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.post("/api/v3/me/bag/checkin1day/use", p::oneDay, auth::activityWrite, writes::handle);
        Dispatcher.post("/api/v3/me/bag/checkin2days/use", p::twoDays, auth::activityWrite, writes::handle);
        Dispatcher.post("/api/v3/me/bag/patchCheckinCard/use", p::patch, auth::activityWrite, writes::handle);
        Dispatcher.post("/api/v3/me/bag/nameCard/use", p::name, auth::activityWrite, auth::settingsWrite, writes::handle);
    }
    public void oneDay(final RequestContext c) { use(c, "checkin1day"); }
    public void twoDays(final RequestContext c) { use(c, "checkin2days"); }
    public void patch(final RequestContext c) {
        ApiV3Requests.respond(c, () -> {
            ApiV3WriteInputs.body(c, Set.of(), true);
            return management.usePatch(ApiV3Requests.viewer(c).optString(Keys.OBJECT_ID), ApiV3WriteInputs.idempotencyKey(c));
        });
    }
    public void name(final RequestContext c) {
        ApiV3Requests.respond(c, () -> {
            final var body = ApiV3WriteInputs.body(c, Set.of("userName"), false);
            return management.useNameCard(ApiV3Requests.viewer(c).optString(Keys.OBJECT_ID),
                    ApiV3WriteInputs.string(body, "userName", 64, true), ApiV3WriteInputs.idempotencyKey(c));
        });
    }
    private void use(final RequestContext c, final String item) {
        ApiV3Requests.respond(c, () -> {
            ApiV3WriteInputs.body(c, Set.of(), true);
            return management.useExemption(ApiV3Requests.viewer(c).optString(Keys.OBJECT_ID), item, ApiV3WriteInputs.idempotencyKey(c));
        });
    }
}
