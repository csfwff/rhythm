package org.b3log.symphony.util;

import java.util.function.BooleanSupplier;

/** Explicit restriction expiries, without legacy negative-second sentinel collisions or database writes. */
public final class ChatMessageRestrictions {
    public record State(long globalMuteUntil, long personalMuteUntil, long riskUntil) { }
    private ChatMessageRestrictions() { }
    public static void check(final State state, final boolean operator, final String content, final long now,
                             final BooleanSupplier confirmation) {
        if (state.riskUntil() > now) {
            if (content.startsWith("![") && content.endsWith(")")) {
                throw new ApiV3Exception(403, "risk_content_rejected", "风控期间不能发送单图片消息");
            }
            if (content.length() < 5) throw new ApiV3Exception(403, "risk_content_rejected", "风控期间消息至少需要5个字符");
            if (!confirmation.getAsBoolean()) throw new ApiV3Exception(409, "confirmation_required", "请使用原请求再次发送，确认本条消息");
        }
        // Preserve existing global-mute priority and operator exemption while a global mute is active.
        if (state.globalMuteUntil() > now) {
            if (!operator) throw new ApiV3Exception(403, "chat_muted", "聊天室正在全员禁言");
        } else if (state.personalMuteUntil() > now) {
            throw new ApiV3Exception(403, "chat_muted", "当前账号正在禁言中");
        }
    }
}
