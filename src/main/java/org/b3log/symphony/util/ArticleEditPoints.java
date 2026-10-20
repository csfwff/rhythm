package org.b3log.symphony.util;

import java.util.ArrayList;
import java.util.List;

/** Existing edit fees, including the distinct preflight balance threshold during the free-edit window. */
public final class ArticleEditPoints {
    private ArticleEditPoints() { }
    public static int fee(final int base, final long followers, final long collections, final long watchers) {
        if (base < 0 || followers < 0 || collections < 0 || watchers < 0) throw new IllegalArgumentException("negative edit input");
        final long subscriptions = Math.multiplyExact(Math.addExact(collections, watchers), 2L);
        return Math.toIntExact(Math.addExact(Math.addExact((long) base, Math.round(Math.sqrt(followers))), subscriptions));
    }
    public static boolean pastFreeWindow(final long created, final long now) { return now - created > 300_000L; }
    public static ArticlePublishPoints.Plan plan(final boolean anonymous, final boolean pastFreeWindow,
                                                final int editFee, final boolean enableReward, final int activationFee,
                                                final int editType, final int rewardType) {
        if (editFee < 0 || activationFee < 0) throw new IllegalArgumentException("negative edit fee");
        if (anonymous) return new ArticlePublishPoints.Plan(0, List.of());
        final var debits = new ArrayList<ArticlePublishPoints.Debit>();
        if (pastFreeWindow) debits.add(new ArticlePublishPoints.Debit(editType, editFee));
        if (enableReward) debits.add(new ArticlePublishPoints.Debit(rewardType, activationFee));
        return new ArticlePublishPoints.Plan(editFee, debits);
    }
}
