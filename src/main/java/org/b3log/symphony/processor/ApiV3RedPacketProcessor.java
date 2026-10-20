package org.b3log.symphony.processor;

import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.util.ApiV3RedPacketTypes;
import org.b3log.symphony.util.ApiV3Requests;

/** Red-packet discovery only. Creation and claims require a separate atomic implementation. */
@Singleton
public class ApiV3RedPacketProcessor {
    public static void register() {
        final var beans = BeanManager.getInstance();
        final var processor = beans.getReference(ApiV3RedPacketProcessor.class);
        final var auth = beans.getReference(ApiV3AuthMidware.class);
        Dispatcher.get("/api/v3/chat/red-packet-types", processor::types, auth::contentRead);
    }

    public void types(final RequestContext context) {
        ApiV3Requests.read(context, ApiV3RedPacketTypes::catalog);
    }
}
