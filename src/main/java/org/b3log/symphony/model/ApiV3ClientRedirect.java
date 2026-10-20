package org.b3log.symphony.model;

/** Exact registered callback; not a prefix or client-provided redirect allow-list. */
public final class ApiV3ClientRedirect {
    private ApiV3ClientRedirect(){ }
    public static final String TABLE="api_v3_client_redirect";
    public static final String CLIENT_ID="clientId",REDIRECT_URI="redirectUri",STATUS="status";
    public static final int ACTIVE=0;
}
