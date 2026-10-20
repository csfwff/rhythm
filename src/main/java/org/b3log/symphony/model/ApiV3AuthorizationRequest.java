package org.b3log.symphony.model;

/** Server-owned, short-lived explicit-consent request. Raw handles/proofs are never persisted. */
public final class ApiV3AuthorizationRequest {
    private ApiV3AuthorizationRequest(){ }
    public static final String TABLE="api_v3_authorization_request";
    public static final String USER_ID="userId",CLIENT_ID="clientId",REDIRECT_URI="redirectUri",SCOPE="scope",STATE="state",
            CHALLENGE="codeChallenge",CONSENT_HASH="consentHash",CREATED_AT="createdAt",EXPIRES_AT="expiresAt",DECIDED_AT="decidedAt",DECISION="decision";
    public static final String PENDING="pending",APPROVED="approved",DENIED="denied";
}
