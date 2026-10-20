package org.b3log.symphony.util;

import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.symphony.model.Follow;
import org.json.JSONObject;

import java.util.List;

/** Relationship transition shared by every writer. Transaction ownership stays with the caller. */
public final class FollowMutation {
    private FollowMutation() { }
    public interface Store {
        boolean hasTransaction();
        boolean lockActor(String actor) throws RepositoryException;
        JSONObject lockTarget(String target, int type) throws RepositoryException;
        List<JSONObject> relations(String actor, String target, int type) throws RepositoryException;
        void add(JSONObject relation) throws RepositoryException;
        void remove(String id) throws RepositoryException;
        void adjust(JSONObject target, String id, int type, int delta) throws RepositoryException;
    }
    @FunctionalInterface public interface TargetCheck { void check(JSONObject target) throws RepositoryException; }
    public static boolean set(final Store store, final String actor, final String targetId, final int type,
                              final boolean enabled, final TargetCheck check) throws RepositoryException {
        if (!store.hasTransaction()) throw new IllegalStateException("follow transaction required");
        if (type < Follow.FOLLOWING_TYPE_C_USER || type > Follow.FOLLOWING_TYPE_C_ARTICLE_WATCH) throw new IllegalArgumentException("Unknown follow type");
        if (!store.lockActor(actor)) throw new RepositoryException("Follower not found");
        final JSONObject target = store.lockTarget(targetId, type);
        if (enabled && null != check) check.check(target);
        if (enabled && null == target) return false;
        final List<JSONObject> existing = store.relations(actor, targetId, type);
        if ((enabled && !existing.isEmpty()) || (!enabled && existing.isEmpty())) return false;
        if (enabled) store.add(new JSONObject().put(Follow.FOLLOWER_ID, actor).put(Follow.FOLLOWING_ID, targetId).put(Follow.FOLLOWING_TYPE, type));
        else for (final JSONObject relation : existing) store.remove(relation.optString(Keys.OBJECT_ID));
        if (null != target && type != Follow.FOLLOWING_TYPE_C_USER) store.adjust(target, targetId, type, enabled ? 1 : -existing.size());
        return true;
    }
}
