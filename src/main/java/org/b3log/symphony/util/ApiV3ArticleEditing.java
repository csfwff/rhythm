package org.b3log.symphony.util;

import org.b3log.symphony.model.Article;
import org.json.JSONObject;

/** Owned edit transition; never returns the private persistence object. */
public final class ApiV3ArticleEditing {
    private ApiV3ArticleEditing() { }
    public record State(JSONObject article, JSONObject chapter) { }
    public record Saved(String id, String permalink) { }
    public interface Store {
        boolean inTransaction();
        State lockAndLoad() throws Exception;
        void validate(JSONObject request) throws Exception;
        Saved save(JSONObject request) throws Exception;
    }
    public static JSONObject apply(final Store store, final String actor, final JSONObject patch) throws Exception {
        if (!store.inTransaction() || !ArticlePublicationCaches.active()) throw new IllegalStateException("article edit transaction required");
        final State state = store.lockAndLoad();
        if (null == state || null == state.article() || !actor.equals(state.article().optString(Article.ARTICLE_AUTHOR_ID))) {
            throw new ApiV3Exception(404, "not_found", "文章不存在或不可编辑");
        }
        if (state.article().optInt(Article.ARTICLE_STATUS) != Article.ARTICLE_STATUS_C_VALID) throw new ApiV3Exception(403, "article_locked", "文章不可编辑");
        final JSONObject request = ApiV3ArticleEditInputs.merge(state.article(), state.chapter(), patch);
        store.validate(request);
        final Saved saved = store.save(request);
        return new JSONObject().put("id", saved.id()).put("permalink", saved.permalink());
    }
}
