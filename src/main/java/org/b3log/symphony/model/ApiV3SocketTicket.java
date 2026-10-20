package org.b3log.symphony.model;

/** One-use socket grants; plaintext credentials never enter persistence. */
public final class ApiV3SocketTicket {
    private ApiV3SocketTicket() { }
    public static final String REPOSITORY = "api_v3_socket_ticket";
    public static final String USER_ID = "userId";
    public static final String PARENT_TOKEN_ID = "parentTokenId";
    public static final String CHANNEL = "channel";
    public static final String TARGET_ID = "targetId";
    public static final String CHANNEL_NOTIFICATIONS = "notifications";
    public static final String CHANNEL_ARTICLE = "article";
    public static final String CHANNEL_CHAT = "chat";
    public static final String CHANNEL_MESSAGES = "messages";
    public static final String CREATED_AT = "createdAt";
    public static final String EXPIRES_AT = "expiresAt";
    public static final String CONSUMED_AT = "consumedAt";
}
