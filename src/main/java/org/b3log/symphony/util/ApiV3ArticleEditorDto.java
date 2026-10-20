package org.b3log.symphony.util;

import org.b3log.latke.Keys;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.LongArticleColumn;
import org.json.JSONObject;

/** Owned editor source only. Raw Markdown/thought records must never be treated as rendered HTML. */
public final class ApiV3ArticleEditorDto {
    private ApiV3ArticleEditorDto() { }
    public static String sql(final String articleTable, final String chapterTable, final String columnTable) {
        for (final String table : new String[]{articleTable, chapterTable, columnTable}) {
            if (null == table || !table.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException("invalid internal table identifier");
        }
        return "SELECT a.oId,a.articleTitle,a.articleContent,a.articleTags,a.articleType,a.articleRewardContent,"
                + "a.articleRewardPoint,a.articleQnAOfferPoint,a.articleCommentable,a.articleShowInList,a.articleStatement,"
                + "a.articleAnonymous,a.articleStatus,a.articleCreateTime,a.articleUpdateTime,a.articlePermalink,a.articleEditorType,"
                + "c.oId AS editorColumnId,c.columnTitle AS editorColumnTitle,c.columnCoverURL AS editorColumnCoverURL,"
                + "CASE WHEN c.oId IS NOT NULL THEN h.chapterNo ELSE NULL END AS editorChapterNo "
                + "FROM `" + articleTable + "` a LEFT JOIN `" + chapterTable + "` h ON h.articleId=a.oId "
                + "LEFT JOIN `" + columnTable + "` c ON c.oId=h.columnId AND c.columnAuthorId=a.articleAuthorId AND c.columnStatus=" + LongArticleColumn.COLUMN_STATUS_C_VALID + " "
                + "WHERE a.oId=? AND a.articleAuthorId=? LIMIT 1";
    }
    public static JSONObject from(final JSONObject row) {
        final Object commentable = row.opt(Article.ARTICLE_COMMENTABLE);
        return new JSONObject().put("id", row.optString(Keys.OBJECT_ID))
                .put("title", row.optString(Article.ARTICLE_TITLE)).put("content", row.optString(Article.ARTICLE_CONTENT))
                .put("tags", row.optString(Article.ARTICLE_TAGS)).put("type", row.optInt(Article.ARTICLE_TYPE))
                .put("rewardContent", row.optString(Article.ARTICLE_REWARD_CONTENT)).put("rewardPoint", row.optInt(Article.ARTICLE_REWARD_POINT))
                .put("qnaOfferPoint", row.optInt(Article.ARTICLE_QNA_OFFER_POINT))
                .put("commentable", commentable instanceof Number n ? n.intValue() != 0 : row.optBoolean(Article.ARTICLE_COMMENTABLE, true))
                .put("showInList", row.optInt(Article.ARTICLE_SHOW_IN_LIST)).put("statement", row.optInt(Article.ARTICLE_STATEMENT))
                .put("anonymous", Article.ARTICLE_ANONYMOUS_C_ANONYMOUS == row.optInt(Article.ARTICLE_ANONYMOUS))
                .put("status", row.optInt(Article.ARTICLE_STATUS)).put("editorType", row.optInt(Article.ARTICLE_EDITOR_TYPE))
                .put("createdAt", row.optLong(Article.ARTICLE_CREATE_TIME)).put("updatedAt", row.optLong(Article.ARTICLE_UPDATE_TIME))
                .put("permalink", row.optString(Article.ARTICLE_PERMALINK))
                .put("columnId", row.optString("editorColumnId")).put("columnTitle", row.optString("editorColumnTitle"))
                .put("columnCoverURL", row.optString("editorColumnCoverURL")).put("chapterNo", row.optString("editorChapterNo"));
    }
}
