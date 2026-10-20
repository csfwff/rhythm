package org.b3log.symphony.model;

/** API v3 access and refresh token fields. Plain tokens are never persisted. */
public final class ApiV3Token {
    private ApiV3Token() {
    }

    public static final String API_V3_TOKEN = "api_v3_token";
    public static final String TOKEN_HASH = "tokenHash";
    public static final String TOKEN_TYPE = "tokenType";
    public static final String USER_ID = "userId";
    public static final String CLIENT_ID = "clientId";
    public static final String SCOPE = "scope";
    public static final String FAMILY_ID = "familyId";
    public static final String PARENT_ID = "parentId";
    public static final String STATE = "state";
    public static final String CREATED_AT = "createdAt";
    public static final String UPDATED_AT = "updatedAt";
    public static final String LAST_USED_AT = "lastUsedAt";
    public static final String EXPIRES_AT = "expiresAt";

    public static final String TYPE_ACCESS = "access";
    public static final String TYPE_REFRESH = "refresh";
    public static final int STATE_ACTIVE = 0;
    public static final int STATE_ROTATED = 1;
    public static final int STATE_REVOKED = 2;
    public static final int STATE_EXPIRED = 3;
}
