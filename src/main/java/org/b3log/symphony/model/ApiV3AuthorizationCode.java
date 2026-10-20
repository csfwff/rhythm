package org.b3log.symphony.model;

/** Hash-only, one-use OAuth authorization code and the grant it approved. */
public final class ApiV3AuthorizationCode {
    private ApiV3AuthorizationCode(){ }
    public static final String TABLE="api_v3_authorization_code";
    public static final String CLIENT_ID="clientId",USER_ID="userId",REDIRECT_URI="redirectUri",SCOPE="scope",
            CHALLENGE="codeChallenge",CREATED_AT="createdAt",EXPIRES_AT="expiresAt",CONSUMED_AT="consumedAt",FAMILY_ID="familyId";
}
