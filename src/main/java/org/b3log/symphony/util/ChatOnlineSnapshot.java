package org.b3log.symphony.util;

import java.util.List;

/** Atomically published remote membership, containing no node addresses or credentials. */
public final class ChatOnlineSnapshot {
    private ChatOnlineSnapshot() { }
    public record Sample(List<String> names, long sampledAt, boolean complete) {
        public Sample { names = List.copyOf(names); }
    }
    private static volatile Sample current = new Sample(List.of(), 0, false);
    public static Sample current() { return current; }
    public static void publish(final List<String> names, final long sampledAt, final boolean complete) {
        current = new Sample(names, sampledAt, complete);
    }
}
