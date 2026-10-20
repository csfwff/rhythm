package org.b3log.symphony.util;


/** Locking read statements used by comment removal. Every external value is bound separately. */
public final class CommentRemovalQueries {
    private CommentRemovalQueries() { }
    private static String table(final String name) {
        if (null == name || !name.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException("invalid internal table identifier");
        return "`" + name + "`";
    }
    public static String replies(final String name) {
        return "SELECT oId FROM " + table(name) + " WHERE commentOriginalCommentId=? LIMIT 1 FOR UPDATE";
    }
    public static String votes(final String name) {
        return "SELECT oId FROM " + table(name) + " WHERE dataId=? AND dataType=? LIMIT 1 FOR UPDATE";
    }
    public static String latest(final String name) {
        return "SELECT oId,commentAuthorId,commentAnonymous FROM " + table(name)
                + " WHERE commentOnArticleId=? ORDER BY oId DESC LIMIT 1 FOR UPDATE";
    }
    public static String revisions(final String name) {
        return "SELECT oId FROM " + table(name) + " WHERE revisionDataId=? AND revisionDataType=? ORDER BY oId FOR UPDATE";
    }
    public static String relations(final String name) {
        return "SELECT oId FROM " + table(name) + " WHERE article_oId=? ORDER BY oId FOR UPDATE";
    }
    public static String userName(final String name) {
        return "SELECT userName FROM " + table(name) + " WHERE oId=? LIMIT 1";
    }
}
