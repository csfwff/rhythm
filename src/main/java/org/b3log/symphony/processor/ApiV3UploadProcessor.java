package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.processor.middleware.ApiV3WriteMidware;
import org.b3log.symphony.service.ApiV3UploadTicketService;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import java.util.Set;

@Singleton
public class ApiV3UploadProcessor {
    @Inject private ApiV3UploadTicketService tickets;
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var processor = beans.getReference(ApiV3UploadProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        final var writes = beans.getReference(ApiV3WriteMidware.class);
        Dispatcher.post("/api/v3/uploads/tickets", processor::ticket, auth::uploadsWrite, writes::handle);
    }
    public void ticket(final RequestContext context) {
        ApiV3Requests.respond(context, () -> {
            ApiV3WriteInputs.body(context, Set.of(), true);
            return tickets.issue(ApiV3Requests.viewer(context).getString(Keys.OBJECT_ID));
        });
    }
}
