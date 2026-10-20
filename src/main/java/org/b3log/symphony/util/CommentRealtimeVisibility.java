package org.b3log.symphony.util;

import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.json.JSONObject;

/** Make recipient-local data before either HTML rendering or public field projection. */
public final class CommentRealtimeVisibility {
    private CommentRealtimeVisibility() { }

    public static JSONObject forRecipient(final JSONObject source, final JSONObject article, final JSONObject viewer) {
        final JSONObject message = new JSONObject(source.toString());
        // Do not retain the publication-time article snapshot in the template model.
        message.put(Article.ARTICLE, new JSONObject(article.toString()));
        if (!ApiV3ContentUtil.canViewCommentContent(source, article, viewer)) {
            message.put(Comment.COMMENT_CONTENT, "");
        }
        return message;
    }

    /** Apply after permission checks and template rendering, when real identity is no longer needed. */
    public static JSONObject publicPayload(final JSONObject selected) {
        final JSONObject payload = new JSONObject(selected.toString());
        if (payload.optInt(Comment.COMMENT_ANONYMOUS) == Comment.COMMENT_ANONYMOUS_C_ANONYMOUS) {
            payload.put(Comment.COMMENT_AUTHOR_ID, "");
        }
        return payload;
    }
}
