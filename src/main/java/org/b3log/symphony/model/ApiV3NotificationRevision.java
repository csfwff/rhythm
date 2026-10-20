package org.b3log.symphony.model;

/** Per-owner committed notification invalidation marker; no message contents or credentials. */
public final class ApiV3NotificationRevision {
    private ApiV3NotificationRevision() { }
    public static final String REPOSITORY = "api_v3_notification_revision";
    public static final String REVISION = "revision";
    public static final String UPDATED_AT = "updatedAt";
}
