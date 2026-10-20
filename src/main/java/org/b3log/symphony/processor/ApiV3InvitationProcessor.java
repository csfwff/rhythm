package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3InvitationQueryService;
import org.b3log.symphony.util.ApiV3Invitation;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import java.util.Set;

/** POST keeps the supplied invitation code out of URLs; the operation is read-only. */
@Singleton
public class ApiV3InvitationProcessor {
    private static final int MAX_BODY_BYTES = 1024;
    @Inject private ApiV3InvitationQueryService query;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var p = beans.getReference(ApiV3InvitationProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        final var budget = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.post("/api/v3/invitation-codes/status", p::status, auth::activityRead, budget::handle);
    }
    public void status(final RequestContext c) {
        ApiV3Requests.read(c, () -> {
            final byte[] bytes = c.getRequest().getBytes();
            if (bytes != null && bytes.length > MAX_BODY_BYTES) throw new ApiV3Exception(413, "body_too_large", "请求体过大");
            return query.status(ApiV3Requests.viewer(c).getString(Keys.OBJECT_ID),
                    ApiV3Invitation.code(ApiV3WriteInputs.body(c, Set.of("code"), false)));
        });
    }
}
