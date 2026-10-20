package org.b3log.symphony.util;

import java.util.ArrayList;
import java.util.List;

/** Existing publication reservation and payment rules; the anonymous reservation is intentionally not a debit. */
public final class CommentPublishPoints {
    private CommentPublishPoints() { }
    public record Rates(int normal, int self, int privateExtra, int anonymousThreshold, int commentType, int privateType) {
        public Rates {
            if (normal < 0 || self < 0 || privateExtra < 0 || anonymousThreshold < 0) throw new IllegalArgumentException("negative comment publication rate");
        }
    }
    public record Debit(String recipient, int type, int amount) {
        public Debit { if (amount < 0) throw new IllegalArgumentException("negative comment debit"); }
    }
    public record Plan(long requiredBalance, List<Debit> debits) {
        public Plan {
            if (requiredBalance < 0) throw new IllegalArgumentException("negative comment reservation");
            debits = List.copyOf(debits);
        }
    }
    public static Plan plan(final Rates rates, final String actor, final String articleAuthor, final String system,
                            final boolean anonymousComment, final boolean anonymousArticle, final boolean privateComment) {
        final boolean self = actor.equals(articleAuthor);
        final int base = self ? rates.self() : rates.normal();
        final long reservation = (long) base + (privateComment ? rates.privateExtra() : 0);
        final long required = Math.max(reservation, anonymousComment ? rates.anonymousThreshold() : 0L);
        final var debits = new ArrayList<Debit>();
        if (!anonymousComment && !anonymousArticle) {
            debits.add(new Debit(self ? system : articleAuthor, rates.commentType(), base));
            if (privateComment) debits.add(new Debit(system, rates.privateType(), rates.privateExtra()));
        }
        return new Plan(required, debits);
    }
    public record Payment(String id, String recipient) { }
    public interface Store {
        boolean inTransaction();
        int lockedBalance() throws Exception;
        String debit(Debit debit) throws Exception;
    }
    public static final class InsufficientBalance extends RuntimeException { }
    /** The caller owns commit/rollback and has already acquired all account-pair locks. */
    public static List<Payment> charge(final Store store, final Plan plan) throws Exception {
        if (!store.inTransaction()) throw new IllegalStateException("comment publication transaction required");
        if (store.lockedBalance() < plan.requiredBalance()) throw new InsufficientBalance();
        final var payments = new ArrayList<Payment>();
        for (final var debit : plan.debits()) {
            final String id = store.debit(debit);
            if (null == id || id.isBlank()) throw new IllegalStateException("comment publication transfer failed");
            payments.add(new Payment(id, debit.recipient()));
        }
        return List.copyOf(payments);
    }

}
