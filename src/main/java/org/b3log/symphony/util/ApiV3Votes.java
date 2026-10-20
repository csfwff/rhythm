package org.b3log.symphony.util;

import org.b3log.symphony.model.Vote;
import org.json.JSONObject;
import java.util.Set;

/** Shared forum vote contract; public state never includes actor or author identities. */
public final class ApiV3Votes {
    private ApiV3Votes() { }
    public static final Set<String> FIELDS = Set.of("vote");
    public static int choice(final JSONObject body) {
        if (!body.keySet().equals(FIELDS) || !(body.opt("vote") instanceof String value)) throw ApiV3WriteInputs.invalid();
        return switch (value) {
            case "up" -> Vote.TYPE_C_UP;
            case "down" -> Vote.TYPE_C_DOWN;
            default -> throw ApiV3WriteInputs.invalid();
        };
    }
    public static Object value(final int choice) {
        return switch (choice) {
            case -1 -> JSONObject.NULL;
            case Vote.TYPE_C_UP -> "up";
            case Vote.TYPE_C_DOWN -> "down";
            default -> throw new IllegalStateException("invalid stored comment vote");
        };
    }
    public static JSONObject dto(final String id, final int vote, final int upCount, final int downCount) {
        if (upCount < 0 || downCount < 0) throw new IllegalStateException("negative stored comment vote count");
        return new JSONObject().put("id", id).put("vote", value(vote)).put("upCount", upCount).put("downCount", downCount);
    }
    public static String commentReadSql(final String comments, final String votes) {
        for (final String table : new String[]{comments, votes}) {
            if (null == table || !table.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException("invalid internal table identifier");
        }
        return "SELECT c.oId,c.commentAuthorId,c.commentOnArticleId,c.commentStatus,c.commentVisible,c.commentGoodCnt,c.commentBadCnt,v.`type` AS currentVote"
                + " FROM `" + comments + "` c LEFT JOIN `" + votes + "` v ON v.dataId=c.oId AND v.userId=? AND v.dataType=? WHERE c.oId=?";
    }
    public static String articleReadSql(final String articles, final String votes) {
        for (final String table : new String[]{articles, votes}) {
            if (null == table || !table.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException("invalid internal table identifier");
        }
        return "SELECT a.oId,a.articleAuthorId,a.articleStatus,a.articleGoodCnt,a.articleBadCnt,v.`type` AS currentVote"
                + " FROM `" + articles + "` a LEFT JOIN `" + votes + "` v ON v.dataId=a.oId AND v.userId=? AND v.dataType=? WHERE a.oId=?";
    }
}
