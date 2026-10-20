package org.b3log.symphony.model;

/** Committed idempotent v3 mutation results. Keys and bearer credentials are not stored. */
public final class ApiV3Request {
    private ApiV3Request() { }
    public static final String API_V3_REQUEST = "api_v3_request";
    public static final String USER_ID = "userId";
    public static final String OPERATION = "operation";
    public static final String REQUEST_HASH = "requestHash";
    public static final String RESPONSE_JSON = "responseJson";
    public static final String CREATED_AT = "createdAt";
}
