package org.b3log.symphony.processor.middleware;

import org.b3log.latke.http.RequestContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.symphony.processor.ApiV3Processor;
import org.b3log.symphony.service.ApiV3TokenMgmtService;
import org.json.JSONObject;

/** API v3 identity only accepts an Authorization Bearer credential. */
@Singleton
public class ApiV3AuthMidware {
    private static final Logger LOGGER = LogManager.getLogger(ApiV3AuthMidware.class);
    @Inject
    private ApiV3TokenMgmtService apiV3TokenMgmtService;

    /** Public resources may be anonymous, but an explicitly supplied credential must be valid. */
    public void optionalContent(final RequestContext context) {
        optional(context, "content.read");
    }

    public void optionalProfile(final RequestContext context) {
        optional(context, "profile.read");
    }

    private void optional(final RequestContext context, final String scope) {
        if (null == context.header("Authorization")) {
            context.handle();
            return;
        }
        final JSONObject identity = resolve(context);
        if (null == identity) return;
        if (!ApiV3TokenMgmtService.hasScope(identity.optString("scope"), scope)) {
            LOGGER.info("api_v3_auth result=insufficient_scope required={} requestId={}", scope, ApiV3Processor.requestId(context));
            ApiV3Processor.error(context, 403, "insufficient_scope", "缺少所需权限");
            context.abort();
            return;
        }
        context.handle();
    }

    public void handle(final RequestContext context) {
        if (null != resolve(context)) context.handle();
    }

    public void messagesRead(final RequestContext context) { required(context, "messages.read"); }
    public void messagesWrite(final RequestContext context) { required(context, "messages.write"); }

    public void notificationsRead(final RequestContext context) { required(context, "notifications.read"); }
    public void notificationsWrite(final RequestContext context) { required(context, "notifications.write"); }
    public void commentsWrite(final RequestContext context) { required(context, "comments.write"); }
    public void reactionsWrite(final RequestContext context) { required(context, "reactions.write"); }
    public void followsWrite(final RequestContext context) { required(context, "follows.write"); }
    public void settingsWrite(final RequestContext context) { required(context, "settings.write"); }
    public void uploadsWrite(final RequestContext context) { required(context, "uploads.write"); }
    public void contentWrite(final RequestContext context) { required(context, "content.write"); }
    public void contentRead(final RequestContext context) { required(context, "content.read"); }
    public void activityWrite(final RequestContext context) { required(context, "activity.write"); }
    public void activityRead(final RequestContext context) { required(context, "activity.read"); }
    public void profileRead(final RequestContext context) { required(context, "profile.read"); }

    private void required(final RequestContext context, final String scope) {
        final JSONObject identity = resolve(context);
        if (null == identity) return;
        if (!ApiV3TokenMgmtService.hasScope(identity.optString("scope"), scope)) {
            LOGGER.info("api_v3_auth result=insufficient_scope required={} requestId={}", scope, ApiV3Processor.requestId(context));
            ApiV3Processor.error(context, 403, "insufficient_scope", "缺少所需权限");
            context.abort();
            return;
        }
        context.handle();
    }

    private JSONObject resolve(final RequestContext context) {
        final String header = context.header("Authorization");
        if (null == header || !header.startsWith("Bearer ") || header.length() > 300) {
            LOGGER.info("api_v3_auth result=unauthorized requestId={}", ApiV3Processor.requestId(context));
            ApiV3Processor.error(context, 401, "unauthorized", "登录凭据无效");
            context.abort();
            return null;
        }
        final JSONObject identity = apiV3TokenMgmtService.authenticate(header.substring("Bearer ".length()).trim());
        if (null == identity) {
            LOGGER.info("api_v3_auth result=unauthorized requestId={}", ApiV3Processor.requestId(context));
            ApiV3Processor.error(context, 401, "unauthorized", "登录凭据无效");
            context.abort();
            return null;
        }
        context.attr(User.USER, identity.optJSONObject("user"));
        context.attr(ApiV3Processor.IDENTITY, identity);
        return identity;
    }
}
