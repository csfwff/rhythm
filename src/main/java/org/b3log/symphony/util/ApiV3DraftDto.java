package org.b3log.symphony.util;

import org.b3log.latke.Keys;
import org.b3log.symphony.model.ArticleDraft;
import org.json.JSONObject;

/** Explicit owned-draft response fields, shared by reads and mutations. */
public final class ApiV3DraftDto {
    private ApiV3DraftDto() { }
    public static JSONObject from(final JSONObject raw, final boolean detail) {
        final JSONObject dto = new JSONObject().put("id", raw.optString(Keys.OBJECT_ID)).put("title", raw.optString(ArticleDraft.TITLE))
                .put("summary", raw.optString(ArticleDraft.SUMMARY)).put("tags", raw.optString(ArticleDraft.TAGS))
                .put("type", raw.optInt(ArticleDraft.ARTICLE_TYPE)).put("columnId", raw.optString(ArticleDraft.COLUMN_ID))
                .put("columnTitle", raw.optString(ArticleDraft.COLUMN_TITLE)).put("chapterNo", raw.optString(ArticleDraft.CHAPTER_NO))
                .put("createdAt", raw.optLong(ArticleDraft.CREATED_TIME)).put("updatedAt", raw.optLong(ArticleDraft.UPDATED_TIME));
        if (detail) dto.put("content", raw.optString(ArticleDraft.CONTENT)).put("thoughtContent", raw.optString(ArticleDraft.THOUGHT_CONTENT))
                .put("rewardContent", raw.optString(ArticleDraft.REWARD_CONTENT)).put("rewardPoint", raw.optInt(ArticleDraft.REWARD_POINT))
                .put("qnaOfferPoint", raw.optInt(ArticleDraft.QNA_OFFER_POINT)).put("commentable", raw.optBoolean(ArticleDraft.COMMENTABLE))
                .put("anonymous", raw.optBoolean(ArticleDraft.ANONYMOUS)).put("notifyFollowers", raw.optBoolean(ArticleDraft.NOTIFY_FOLLOWERS))
                .put("showInList", raw.optInt(ArticleDraft.SHOW_IN_LIST)).put("statement", raw.optInt(ArticleDraft.STATEMENT));
        return dto;
    }

}
