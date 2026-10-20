package org.b3log.symphony.util;

import org.json.JSONObject;
import java.nio.charset.StandardCharsets;

/** New API ownership is the immutable stored author ID, never the user's mutable name. */
public final class ChatMessageRemovalPolicy {
    private static final int MAX_ENVELOPE_LENGTH = 102400;
    private ChatMessageRemovalPolicy() { }
    public static void requireOwner(final JSONObject row, final String actor) {
        requireRemovable(row, actor, false);
    }
    /** Administrative moderation is selected by a trusted caller after checking the current role. */
    public static void requireRemovable(final JSONObject row, final String actor, final boolean moderator) {
        final JSONObject envelope;
        try {
            final String raw = row == null ? "" : row.optString("content");
            if (raw.isEmpty() || raw.length() > MAX_ENVELOPE_LENGTH) throw new IllegalArgumentException("unavailable message");
            envelope = ApiV3Json.parse(raw.getBytes(StandardCharsets.UTF_8));
        } catch (final IllegalArgumentException e) { throw missing(); }
        if (actor == null || !actor.matches("[1-9][0-9]{0,31}") || !moderator && !actor.equals(envelope.optString("userOId"))) throw missing();
        final Object content = envelope.opt("content");
        JSONObject card = content instanceof JSONObject object ? object : null;
        if (content instanceof String text && text.stripLeading().startsWith("{")) {
            try { card = ApiV3Json.parse(text.getBytes(StandardCharsets.UTF_8)); }
            catch (final IllegalArgumentException ignored) { /* Ordinary messages may contain non-JSON text. */ }
        } else if (!(content instanceof String) && card == null) throw missing();
        if (card != null && "redPacket".equals(card.optString("msgType"))) {
            throw new ApiV3Exception(409, "message_not_removable", "红包消息不能撤回");
        }
    }
    private static ApiV3Exception missing() { return new ApiV3Exception(404, "not_found", "消息不存在或不属于当前用户"); }
}
