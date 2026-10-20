package org.b3log.symphony.util;

import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Reward;
import java.util.*;

/** Typed content notification references. AT records lack a target kind; preserve them on ID collisions. */
public final class ContentNotificationReferences {
    private ContentNotificationReferences() { }
    public record Selection(String sql, List<Object> args) { }
    private static String table(final String name) {
        if (null == name || !name.matches("[A-Za-z0-9_]+")) throw new IllegalArgumentException("invalid internal table identifier");
        return "`" + name + "`";
    }
    public static String oppositeTarget(final String name) {
        return "SELECT oId FROM " + table(name) + " WHERE oId=? LIMIT 1 FOR UPDATE";
    }
    public static Selection target(final String name, final String id, final boolean article, final boolean removeMentions) {
        if (null == id || !id.matches("[0-9]{1,32}")) throw new IllegalArgumentException("invalid content notification identifier");
        final List<Integer> direct = article ? List.of(Notification.DATA_TYPE_C_ARTICLE, Notification.DATA_TYPE_C_FOLLOWING_USER,
                Notification.DATA_TYPE_C_BROADCAST, Notification.DATA_TYPE_C_SYS_ANNOUNCE_ARTICLE,
                Notification.DATA_TYPE_C_FOLLOWING_ARTICLE_UPDATE, Notification.DATA_TYPE_C_POINT_PERFECT_ARTICLE)
                : List.of(Notification.DATA_TYPE_C_COMMENT, Notification.DATA_TYPE_C_COMMENTED,
                Notification.DATA_TYPE_C_REPLY, Notification.DATA_TYPE_C_FOLLOWING_ARTICLE_COMMENT);
        final List<Integer> compound = article ? List.of(Notification.DATA_TYPE_C_ARTICLE_NEW_FOLLOWER,
                Notification.DATA_TYPE_C_ARTICLE_NEW_WATCHER, Notification.DATA_TYPE_C_ARTICLE_VOTE_UP, Notification.DATA_TYPE_C_ARTICLE_VOTE_DOWN)
                : List.of(Notification.DATA_TYPE_C_COMMENT_VOTE_UP, Notification.DATA_TYPE_C_COMMENT_VOTE_DOWN);
        final var args = new ArrayList<Object>();
        args.add(id); args.addAll(direct); args.add(id); args.add(Notification.DATA_TYPE_C_AT); args.add(removeMentions ? 1 : 0);
        args.addAll(compound); args.add(id + "-%");
        return new Selection("SELECT oId,userId FROM " + table(name) + " WHERE (dataId=? AND dataType IN ("
                + String.join(",", Collections.nCopies(direct.size(), "?")) + ")) OR (dataId=? AND dataType=? AND ?=1) OR (dataType IN ("
                + String.join(",", Collections.nCopies(compound.size(), "?")) + ") AND dataId LIKE ?) ORDER BY oId FOR UPDATE", List.copyOf(args));
    }
    public static int rewardNotificationType(final int rewardType) {
        return switch (rewardType) {
            case Reward.TYPE_C_ARTICLE -> Notification.DATA_TYPE_C_POINT_ARTICLE_REWARD;
            case Reward.TYPE_C_THANK_ARTICLE -> Notification.DATA_TYPE_C_POINT_ARTICLE_THANK;
            case Reward.TYPE_C_ACCEPT_COMMENT -> Notification.DATA_TYPE_C_POINT_COMMENT_ACCEPT;
            case Reward.TYPE_C_COMMENT -> Notification.DATA_TYPE_C_POINT_COMMENT_THANK;
            default -> throw new IllegalArgumentException("reward type has no content notification");
        };
    }
}
