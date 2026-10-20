package org.b3log.symphony.util;

import org.b3log.symphony.model.ArticleDraft;
import org.json.JSONObject;

/** Atomic creation body; the store binds Bearer identity, validators and current-transaction services. */
public final class ApiV3ArticleCreation {
    private ApiV3ArticleCreation() { }
    public record Created(String id, String permalink) { }
    public interface Store {
        boolean inTransaction();
        void lockAndAuthorize() throws Exception;
        void validate(JSONObject request) throws Exception;
        Created publish(JSONObject request) throws Exception;
        void removeDraft(String draftId) throws Exception;
    }
    public static JSONObject apply(final Store store, final JSONObject normalized) throws Exception {
        if (!store.inTransaction() || !ArticlePublicationCaches.active()) throw new IllegalStateException("article creation transaction required");
        store.lockAndAuthorize();
        final JSONObject request = new JSONObject(normalized.toString());
        store.validate(request);
        final Created created = store.publish(request);
        final String draft = normalized.optString(ArticleDraft.ARTICLE_DRAFT_ID);
        if (!draft.isEmpty()) store.removeDraft(draft);
        return new JSONObject().put("id", created.id()).put("permalink", created.permalink());
    }
}
