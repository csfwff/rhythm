package org.b3log.symphony.util;

import org.b3log.symphony.model.Comment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/** Identical parameterized selection for paragraph rows and their pagination count. */
public final class ApiV3ParagraphQueries {
    private ApiV3ParagraphQueries() { }
    public record Selection(String sql, List<Object> args) {
        public Selection { args = List.copyOf(args); }
    }
    public static Selection selection(final String table, final String article, final String paragraph, final String state) {
        return selection(table, article, paragraph.isEmpty() ? List.of() : List.of(paragraph), state);
    }
    public static Selection selection(final String table, final String article, final List<String> paragraphs, final String state) {
        if (!Set.of("active", "orphaned", "all").contains(state)
                || paragraphs.size() > 2 || paragraphs.stream().anyMatch(id -> !id.matches("[0-9a-f]{64}"))) throw ApiV3WriteInputs.invalid();
        final var args = new ArrayList<Object>(List.of(article, Comment.COMMENT_TYPE_C_PARAGRAPH, Comment.COMMENT_STATUS_C_VALID));
        final var sql = new StringBuilder(" FROM `" + table + "` WHERE commentOnArticleId=? AND commentType=? AND commentStatus=?");
        if ("all".equals(state)) {
            sql.append(" AND commentParagraphStatus IN (?,?)");
            args.add(Comment.COMMENT_PARAGRAPH_STATUS_C_ACTIVE);
            args.add(Comment.COMMENT_PARAGRAPH_STATUS_C_ORPHANED);
        } else {
            sql.append(" AND commentParagraphStatus=?");
            args.add("active".equals(state) ? Comment.COMMENT_PARAGRAPH_STATUS_C_ACTIVE : Comment.COMMENT_PARAGRAPH_STATUS_C_ORPHANED);
        }
        if (!paragraphs.isEmpty()) {
            sql.append(" AND commentParagraphId IN (").append(String.join(",", Collections.nCopies(paragraphs.size(), "?"))).append(")");
            args.addAll(paragraphs);
        }
        return new Selection(sql.toString(), args);
    }
    public static Selection page(final Selection selection, final int p, final int size, final String sort) {
        if (p < 1 || p > Integer.MAX_VALUE / 50 || size < 1 || size > 50) throw ApiV3WriteInputs.invalid();
        final String order = switch (sort) {
            case "oldest" -> "oId ASC";
            case "latest" -> "oId DESC";
            case "hot" -> "commentScore DESC,oId DESC";
            default -> throw ApiV3WriteInputs.invalid();
        };
        final var args = new ArrayList<>(selection.args()); args.add((long) (p - 1) * size); args.add(size);
        final String fields = "oId,commentOnArticleId,commentAuthorId,commentOriginalCommentId,commentContent,commentCreateTime,"
                + "commentAnonymous,commentVisible,commentStatus,commentType,commentParagraphId,commentParagraphKind,"
                + "commentParagraphIndex,commentParagraphStatus";
        return new Selection("SELECT " + fields + selection.sql() + " ORDER BY " + order + " LIMIT ?,?", args);
    }
    public static Selection counts(final String table, final String article, final List<String> paragraphIds) {
        if (paragraphIds.size() > 50) throw ApiV3WriteInputs.invalid();
        return aliasCounts(table, article, paragraphIds);
    }
    public static Selection aliasCounts(final String table, final String article, final List<String> paragraphIds) {
        if (paragraphIds.isEmpty() || paragraphIds.size() > 100
                || paragraphIds.stream().anyMatch(id -> !id.matches("[0-9a-f]{64}"))) throw ApiV3WriteInputs.invalid();
        final var args = new ArrayList<Object>(List.of(article, Comment.COMMENT_TYPE_C_PARAGRAPH,
                Comment.COMMENT_STATUS_C_VALID, Comment.COMMENT_PARAGRAPH_STATUS_C_ACTIVE));
        args.addAll(paragraphIds);
        return new Selection("SELECT commentParagraphId,COUNT(1) AS commentCount FROM `" + table
                + "` WHERE commentOnArticleId=? AND commentType=? AND commentStatus=? AND commentParagraphStatus=?"
                + " AND commentParagraphId IN (" + String.join(",", Collections.nCopies(paragraphIds.size(), "?"))
                + ") GROUP BY commentParagraphId", args);
    }
}
