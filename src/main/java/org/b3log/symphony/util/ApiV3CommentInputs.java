package org.b3log.symphony.util;

import org.b3log.symphony.model.Comment;
import org.json.JSONObject;
import java.util.Set;

/** Public comment fields; identities, moderation state and paragraph snapshots are server-owned. */
public final class ApiV3CommentInputs {
    private ApiV3CommentInputs() { }
    public static final Set<String> EDIT_FIELDS = Set.of("content", "visible");
    public static final Set<String> CREATE_FIELDS = Set.of("content", "visible", "anonymous", "parentId", "paragraphId");
    public static JSONObject create(final JSONObject body) {
        if (!CREATE_FIELDS.containsAll(body.keySet())) throw ApiV3WriteInputs.invalid();
        final var text = new JSONObject();
        if (body.has("content")) text.put("content", body.opt("content"));
        final JSONObject request = edit(text);
        request.put(Comment.COMMENT_VISIBLE, ApiV3WriteInputs.bool(body, "visible", false)
                ? Comment.COMMENT_VISIBLE_C_AUTHOR : Comment.COMMENT_VISIBLE_C_ALL);
        request.put(Comment.COMMENT_ANONYMOUS, ApiV3WriteInputs.bool(body, "anonymous", false)
                ? Comment.COMMENT_ANONYMOUS_C_ANONYMOUS : Comment.COMMENT_ANONYMOUS_C_PUBLIC);
        final String parent = ApiV3WriteInputs.string(body, "parentId", 19, true);
        final String paragraph = ApiV3WriteInputs.string(body, "paragraphId", 64, true);
        if ((!parent.isEmpty() && !parent.matches("[0-9]{1,19}"))
                || (!paragraph.isEmpty() && !paragraph.matches("[0-9a-f]{64}"))) throw ApiV3WriteInputs.invalid();
        return request.put(Comment.COMMENT_ORIGINAL_COMMENT_ID, parent).put(Comment.COMMENT_PARAGRAPH_ID, paragraph);
    }
    public static JSONObject edit(final JSONObject body) {
        if (!EDIT_FIELDS.containsAll(body.keySet()) || !body.has("content")) throw ApiV3WriteInputs.invalid();
        final String content = ApiV3WriteInputs.string(body, "content", Comment.MAX_COMMENT_CONTENT_LENGTH, false);
        if (content.isBlank() || content.codePoints().anyMatch(c -> Character.isISOControl(c) && c != '\n' && c != '\r' && c != '\t')) {
            throw ApiV3WriteInputs.invalid();
        }
        final var request = new JSONObject().put(Comment.COMMENT_CONTENT, content);
        if (body.has("visible")) request.put(Comment.COMMENT_VISIBLE, ApiV3WriteInputs.bool(body, "visible", false)
                ? Comment.COMMENT_VISIBLE_C_AUTHOR : Comment.COMMENT_VISIBLE_C_ALL);
        return request;
    }
}
