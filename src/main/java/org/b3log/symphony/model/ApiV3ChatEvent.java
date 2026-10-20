package org.b3log.symphony.model;

import java.util.Set;

/** Committed public-room changes; no message contents, user snapshots or credentials. */
public final class ApiV3ChatEvent {
    private ApiV3ChatEvent() { }
    public static final String REPOSITORY="api_v3_chat_event";
    public static final String STATE_REPOSITORY="api_v3_chat_state";
    public static final String ROOM="main";
    public static final String TOPIC="topic";
    public static final String TOPIC_TARGET="0";
    public static final Set<String> KINDS=Set.of("created","updated","deleted","reactions",TOPIC);
    public static boolean validChange(final String kind, final String id) {
        return kind != null && KINDS.contains(kind) && id != null && id.matches("[0-9]{1,32}")
                && (!TOPIC.equals(kind) || TOPIC_TARGET.equals(id));
    }
}
