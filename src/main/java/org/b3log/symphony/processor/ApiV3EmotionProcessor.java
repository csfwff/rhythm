package org.b3log.symphony.processor;

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.middleware.ApiV3AuthMidware;
import org.b3log.symphony.service.ApiV3EmotionQueryService;
import org.b3log.symphony.util.ApiV3EmotionOptions;
import org.b3log.symphony.util.ApiV3Requests;

/** Owner-only, read-only emoji settings. */
@Singleton
public class ApiV3EmotionProcessor {
    @Inject private ApiV3EmotionQueryService query;
    public static void register() {
        final BeanManager beans = BeanManager.getInstance();
        final ApiV3EmotionProcessor p = beans.getReference(ApiV3EmotionProcessor.class);
        final ApiV3AuthMidware auth = beans.getReference(ApiV3AuthMidware.class);
        Dispatcher.get("/api/v3/me/settings/emotions", p::emotions, auth::profileRead);
        Dispatcher.get("/api/v3/me/settings/emotion-options", p::options, auth::profileRead);
        Dispatcher.get("/api/v3/me/settings/emoji-groups", p::groups, auth::profileRead);
        Dispatcher.get("/api/v3/me/settings/emoji-groups/{groupId}/emojis", p::items, auth::profileRead);
    }
    public void emotions(final RequestContext c) { ApiV3Requests.read(c, () -> query.emotions(actor(c))); }
    public void options(final RequestContext c) { ApiV3Requests.read(c, ApiV3EmotionOptions::options); }
    public void groups(final RequestContext c) { ApiV3Requests.read(c, () -> query.groups(actor(c), ApiV3Requests.page(c), ApiV3Requests.size(c))); }
    public void items(final RequestContext c) { ApiV3Requests.read(c, () -> query.items(actor(c), ApiV3Requests.id(c, "groupId"), ApiV3Requests.page(c), ApiV3Requests.size(c))); }
    private static String actor(final RequestContext c) { return ApiV3Requests.viewer(c).optString(Keys.OBJECT_ID); }
}
