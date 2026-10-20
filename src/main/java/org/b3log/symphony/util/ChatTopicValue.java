package org.b3log.symphony.util;

import org.json.JSONObject;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Set;

/** Durable topic value only; entry points own their approved filtering policy before constructing it. */
public record ChatTopicValue(String text, String whoChanged, long revision) {
    public static final String OPTION_ID="chatRoomTopic";
    public static final int COST=16;
    public ChatTopicValue {
        if(text==null||text.isBlank()||text.length()>64||whoChanged==null||whoChanged.isBlank()||whoChanged.length()>64||revision<1)throw new IllegalArgumentException("topic state");
        // Reject malformed UTF-16 rather than silently persisting a truncated surrogate or ambiguous JSON.
        ApiV3Json.canonical(new JSONObject().put("text",text).put("whoChanged",whoChanged));
    }
    public String stored() {
        final String value=new JSONObject().put("text",text).put("whoChanged",whoChanged).put("revision",Long.toString(revision)).toString();
        if(value.length()>512)throw new IllegalArgumentException("topic state too long");
        return value;
    }
    public static ChatTopicValue parse(final String raw) {
        try {
            if(raw==null||raw.length()>512)throw new IllegalArgumentException("topic state length");
            final var value=ApiV3Json.parse(raw.getBytes(StandardCharsets.UTF_8));
            if(!value.keySet().equals(Set.of("text","whoChanged","revision"))||!(value.get("text")instanceof String)
                    ||!(value.get("whoChanged")instanceof String)||!(value.get("revision")instanceof String revision)
                    ||!revision.matches("[1-9][0-9]{0,18}"))throw new IllegalArgumentException("topic state shape");
            return new ChatTopicValue(value.getString("text"),value.getString("whoChanged"),new BigDecimal(revision).longValueExact());
        }catch(RuntimeException e){throw new IllegalStateException("invalid stored chat topic",e);}
    }
}
