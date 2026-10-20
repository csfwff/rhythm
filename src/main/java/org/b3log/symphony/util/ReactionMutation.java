package org.b3log.symphony.util;

import org.b3log.latke.Keys;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.symphony.model.Reaction;
import org.json.JSONObject;
import java.util.List;

/** Shared explicit-state/toggle transition. Caller owns the transaction and any postcommit publication. */
public final class ReactionMutation {
    private ReactionMutation() { }
    public interface Store {
        boolean active();
        boolean lockActor(String actor) throws RepositoryException;
        JSONObject lockTarget(String type, String id) throws RepositoryException;
        List<JSONObject> existing(String actor, String type, String id, String group) throws RepositoryException;
        void add(JSONObject row) throws RepositoryException;
        void update(String id, JSONObject row) throws RepositoryException;
        void remove(String id) throws RepositoryException;
    }
    @FunctionalInterface public interface TargetCheck { void check(JSONObject target) throws RepositoryException; }
    public static boolean change(final Store store, final String actor, final String type, final String id,
                                 final String group, final String value, final boolean toggle, final TargetCheck check) throws RepositoryException {
        if (!store.active()) throw new IllegalStateException("reaction transaction required");
        if (null == actor || actor.isEmpty() || !Reaction.isValidTargetType(type) || !Reaction.isValidGroup(group)
                || null == id || id.isEmpty() || null == value || (value.isEmpty() ? toggle : !Reaction.isValidEmojiValue(value))) {
            throw new IllegalArgumentException("Invalid reaction transition");
        }
        if (!store.lockActor(actor)) throw new RepositoryException("Reaction actor not found");
        final JSONObject target = store.lockTarget(type, id);
        if (null != check) check.check(target);
        if (null == target) throw new RepositoryException("Reaction target not found");
        final List<JSONObject> rows = store.existing(actor, type, id, group);
        final JSONObject first = rows.isEmpty() ? null : rows.getFirst();
        final String desired = toggle && null != first && value.equals(first.optString(Reaction.VALUE)) ? "" : value;
        if (desired.isEmpty()) {
            for (final JSONObject row : rows) store.remove(row.optString(Keys.OBJECT_ID));
            return !rows.isEmpty();
        }
        final long now = System.currentTimeMillis();
        if (null == first) {
            store.add(new JSONObject().put(Reaction.USER_ID, actor).put(Reaction.TARGET_TYPE, type).put(Reaction.TARGET_ID, id)
                    .put(Reaction.GROUP, group).put(Reaction.VALUE, desired).put(Reaction.CREATED_TIME, now).put(Reaction.UPDATED_TIME, now));
            return true;
        }
        final boolean changed = !desired.equals(first.optString(Reaction.VALUE));
        if (changed) store.update(first.optString(Keys.OBJECT_ID), new JSONObject(first.toString()).put(Reaction.VALUE, desired).put(Reaction.UPDATED_TIME, now));
        for (int i = 1; i < rows.size(); i++) store.remove(rows.get(i).optString(Keys.OBJECT_ID));
        return changed || rows.size() > 1;
    }
}
