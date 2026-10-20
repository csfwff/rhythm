package org.b3log.symphony.util;

import org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/** v3 ordinary-message boundary. It validates text without executing any legacy command. */
public final class ChatMessageText {
    private static final Set<String> CARDS = Set.of("redPacket", "weather", "music");
    private static final String[] TAGS = {"redpacket", "weather", "music", "setdiscuss", "barrager"};
    private ChatMessageText() { }

    /** Call on both raw and moderated text; reserved-word replacement must not introduce commands. */
    public static void requireOrdinary(final String content) {
        if (content == null || content.isBlank() || content.length() > ChatMessageValidation.MAX_CONTENT_LENGTH) {
            throw new ApiV3Exception(422, "invalid_content", "聊天消息内容为空或过多（大于4096字符）");
        }
        final String text = content.strip();
        boolean reserved = text.startsWith("执法") || text.startsWith("zf");
        for (final String tag : TAGS) {
            if (text.startsWith("[" + tag + "]") && text.endsWith("[/" + tag + "]")) reserved = true;
        }
        if (text.startsWith("{")) {
            JSONObject object = null;
            try { object = ApiV3Json.parse(text.getBytes(StandardCharsets.UTF_8)); }
            catch (final IllegalArgumentException ignored) { /* Invalid JSON remains plain text. */ }
            if (object != null && CARDS.contains(object.optString("msgType"))) reserved = true;
        }
        if (reserved) throw new ApiV3Exception(422, "special_message_required", "普通消息不接受业务命令，请使用对应功能接口");
    }
}
