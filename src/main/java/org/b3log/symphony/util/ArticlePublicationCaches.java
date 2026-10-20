package org.b3log.symphony.util;

import org.apache.logging.log4j.LogManager;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;

/** Opt-in publication boundary: repositories bypass caches until the caller ends its transaction. */
public final class ArticlePublicationCaches implements AutoCloseable {
    private static final ThreadLocal<ArticlePublicationCaches> CURRENT = new ThreadLocal<>();
    private final Map<String, Runnable> invalidations = new LinkedHashMap<>();
    private final BooleanSupplier transactionActive;
    private boolean closed;
    private ArticlePublicationCaches(final BooleanSupplier transactionActive) { this.transactionActive = transactionActive; }
    public static ArticlePublicationCaches open(final BooleanSupplier transactionActive) {
        if (active()) throw new IllegalStateException("publication cache boundary already active");
        final var scope = new ArticlePublicationCaches(transactionActive);
        CURRENT.set(scope);
        return scope;
    }
    public static boolean active() { return null != CURRENT.get(); }
    public static void afterTransaction(final String key, final Runnable action) {
        final var scope = CURRENT.get();
        if (null == scope) throw new IllegalStateException("publication cache boundary required");
        scope.invalidations.put(key, action);
    }
    /** Called after commit or rollback; never publishes the objects modified by the transaction. */
    @Override public void close() {
        if (closed) return;
        if (CURRENT.get() != this) throw new IllegalStateException("publication cache boundary owner mismatch");
        closed = true;
        CURRENT.remove();
        if (transactionActive.getAsBoolean()) throw new IllegalStateException("publication cache refresh before transaction end");
        for (final var action : invalidations.values()) {
            try { action.run(); }
            catch (final RuntimeException e) { LogManager.getLogger(ArticlePublicationCaches.class).error("Publication cache invalidation failed", e); }
        }
    }
}
