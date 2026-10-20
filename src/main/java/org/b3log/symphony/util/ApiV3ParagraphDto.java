package org.b3log.symphony.util;

import org.b3log.latke.Keys;
import org.b3log.symphony.model.Comment;
import org.json.JSONObject;

/** Paragraph metadata is plain text; persistence objects are never serialized directly. */
public final class ApiV3ParagraphDto {
    private ApiV3ParagraphDto() { }
    public static JSONObject paragraph(final String id, final String kind, final int index, final String snapshot,
                                       final boolean orphaned) {
        return new JSONObject().put("id", id).put("kind", kind).put("index", index).put("snapshot", snapshot)
                .put("state", orphaned ? "orphaned" : "active");
    }
    /** safeBase is the existing viewer-authorized commentDto result, never the database comment. */
    public static JSONObject comment(final JSONObject safeBase, final JSONObject row) {
        final boolean paragraph = row.optInt(Comment.COMMENT_TYPE) == Comment.COMMENT_TYPE_C_PARAGRAPH;
        final String id = row.optString(Comment.COMMENT_PARAGRAPH_ID);
        final String anchor = paragraph && id.matches("[0-9a-f]{64}") ? id : "";
        final String permalink = "/article/" + row.optString(Comment.COMMENT_ON_ARTICLE_ID)
                + (anchor.isEmpty() ? "" : "?paragraph=" + anchor) + "#" + row.optString(Keys.OBJECT_ID);
        final JSONObject metadata = paragraph ? paragraph(anchor, row.optString(Comment.COMMENT_PARAGRAPH_KIND),
                row.optInt(Comment.COMMENT_PARAGRAPH_INDEX, -1), "",
                row.optInt(Comment.COMMENT_PARAGRAPH_STATUS) != Comment.COMMENT_PARAGRAPH_STATUS_C_ACTIVE) : null;
        // Historical snapshots can contain automatically expanded restricted article titles.
        // Current paragraph text is available from the authorized article's paragraph list.
        if (null != metadata) metadata.remove("snapshot");
        return new JSONObject(safeBase.toString()).put("type", paragraph ? 1 : 0).put("permalink", permalink)
                .put("paragraph", null == metadata ? JSONObject.NULL : metadata);
    }
}
