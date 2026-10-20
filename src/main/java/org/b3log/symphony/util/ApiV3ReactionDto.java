package org.b3log.symphony.util;

import org.b3log.symphony.model.Reaction;
import org.json.JSONArray;
import org.json.JSONObject;

/** Explicit public projection, including nested tooltip profiles. */
public final class ApiV3ReactionDto {
    private ApiV3ReactionDto() { }
    public static JSONObject from(final JSONObject raw) {
        final JSONArray summary = new JSONArray(), source = raw.optJSONArray(Reaction.RESPONSE_SUMMARY);
        if (null != source) for (int i = 0; i < source.length(); i++) {
            final JSONObject item = source.optJSONObject(i);
            if (null == item || !Reaction.isValidEmojiValue(item.optString("value")) || item.optLong("count") <= 0) continue;
            final JSONArray names = new JSONArray(), profiles = new JSONArray();
            final JSONArray details = item.optJSONArray("userDetails");
            if (null != details) for (int j = 0; j < details.length(); j++) {
                final JSONObject user = details.optJSONObject(j);
                if (null == user) continue;
                final String displayName = user.optString("displayName");
                if (!displayName.isEmpty()) names.put(displayName);
                profiles.put(new JSONObject().put("userName", user.optString("userName")).put("displayName", displayName)
                        .put("avatarURL", ApiV3CommunityDto.safeUrl(user.optString("avatarURL"))));
            }
            summary.put(new JSONObject().put("value", item.optString("value")).put("emoji", Reaction.emojiOf(item.optString("value")))
                    .put("count", item.optLong("count")).put("selected", item.optBoolean("selected"))
                    .put("users", names).put("userDetails", profiles));
        }
        final String current = raw.optString(Reaction.FIELD_CURRENT_USER_REACTION);
        return new JSONObject().put("targetId", raw.optString("targetId")).put("targetType", raw.optString("targetType"))
                .put("groupType", Reaction.GROUP_EMOJI).put("currentUserReaction", Reaction.isValidEmojiValue(current) ? current : "").put("summary", summary);
    }
    public static String value(final JSONObject body) {
        final String value = ApiV3WriteInputs.string(body, "value", 32, true);
        if (!Reaction.isValidEmojiValue(value)) throw ApiV3WriteInputs.invalid();
        return value;
    }
    public static JSONArray emojis() {
        final JSONArray list = new JSONArray();
        for (final String value : Reaction.emojiValues()) list.put(new JSONObject().put("value", value).put("emoji", Reaction.emojiOf(value)));
        return list;
    }
}
