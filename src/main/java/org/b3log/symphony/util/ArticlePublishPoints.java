package org.b3log.symphony.util;

import java.util.ArrayList;
import java.util.List;

/** Publication points transition. The caller owns the business transaction and account-pair lock. */
public final class ArticlePublishPoints {
    private ArticlePublishPoints() { }

    /** Values are supplied from the existing business constants, never from an API request. */
    public record Rates(int publication, int rewardActivation, int broadcast, int anonymousThreshold,
                        int publicationType, int rewardType, int broadcastType) {
        public Rates {
            if (publication < 0 || rewardActivation < 0 || broadcast < 0 || anonymousThreshold < 0) {
                throw new IllegalArgumentException("negative publication rate");
            }
        }
    }
    public record Debit(int type, int amount) {
        public Debit { if (amount < 0) throw new IllegalArgumentException("negative debit"); }
    }
    public record Plan(long requiredBalance, List<Debit> debits) {
        public Plan {
            if (requiredBalance < 0) throw new IllegalArgumentException("negative required balance");
            debits = List.copyOf(debits);
        }
        public long minimumBalance() {
            long total = 0;
            for (final Debit debit : debits) total = Math.addExact(total, debit.amount());
            return Math.max(requiredBalance, total);
        }
    }
    public interface Store {
        boolean inTransaction();
        /** Must lock and read the durable author row, bypassing user/session caches. */
        int lockAuthorBalance() throws Exception;
        /** Must use transferInCurrentTransaction with asynchronous indexing disabled. */
        String debit(int type, int amount) throws Exception;
    }
    public static final class InsufficientBalance extends RuntimeException { }

    /** Preserve the existing reservation rule and its distinct fixed reward activation charge. */
    public static Plan plan(final Rates rates, final boolean anonymous, final boolean cityBroadcast,
                            final long followers, final int rewardPrice, final int qnaOffer) {
        if (followers < 0 || rewardPrice < 0 || qnaOffer < 0) throw new IllegalArgumentException("negative publication input");
        if (anonymous) return new Plan(rates.anonymousThreshold(), List.of());
        final long addition = Math.round(Math.sqrt(followers));
        final int publication = Math.toIntExact(Math.addExact((long) rates.publication(), addition));
        final long required = (long) publication + rewardPrice + qnaOffer + (cityBroadcast ? rates.broadcast() : 0);
        final List<Debit> debits = new ArrayList<>();
        debits.add(new Debit(rates.publicationType(), publication));
        if (rewardPrice > 0) debits.add(new Debit(rates.rewardType(), rates.rewardActivation()));
        if (cityBroadcast) debits.add(new Debit(rates.broadcastType(), rates.broadcast()));
        return new Plan(required, debits);
    }

    /** No commit, rollback, cache publication, index update or event dispatch occurs here. */
    public static List<String> charge(final Store store, final Plan plan) throws Exception {
        if (!store.inTransaction()) throw new IllegalStateException("publication transaction required");
        final int balance = store.lockAuthorBalance();
        if (plan.requiredBalance() == 0 && plan.debits().isEmpty()) return List.of();
        if (balance < plan.minimumBalance()) throw new InsufficientBalance();
        final List<String> transfers = new ArrayList<>();
        for (final Debit debit : plan.debits()) {
            final String transfer = store.debit(debit.type(), debit.amount());
            if (null == transfer || transfer.isBlank()) throw new IllegalStateException("publication transfer failed");
            transfers.add(transfer);
        }
        return List.copyOf(transfers);
    }
}
