package org.b3log.symphony.util;

import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.jdbc.JdbcTransaction;
import org.b3log.latke.repository.jdbc.JdbcRepository;
import java.sql.SQLException;

/** Preserve the bundled JDBC transaction's ownership/disposal, but propagate commit failures. */
public final class ApiV3Transactions {
    private ApiV3Transactions() { }
    /** Background and socket callbacks have no HTTP end handler to return thread-local JDBC connections. */
    public static void releaseThreadResources(){
        try{
            final JdbcTransaction transaction=JdbcRepository.TX.get();
            if(transaction!=null&&transaction.isActive())transaction.rollback();
        }finally{JdbcRepository.dispose();}
    }
    public static Transaction strict(final Transaction transaction) {
        if (null == transaction) throw new IllegalArgumentException("transaction required");
        if (!(transaction instanceof JdbcTransaction jdbc)) return transaction;
        return new Transaction() {
            @Override public String getId() { return jdbc.getId(); }
            @Override public boolean isActive() { return jdbc.isActive(); }
            @Override public void rollback() { jdbc.rollback(); }
            @Override public void commit() {
                if (!jdbc.isActive()) throw new IllegalStateException("transaction is not active");
                try {
                    // JdbcTransaction.commit logs and swallows SQLException. Keep its connection
                    // alive on failure so the owning service's rollback/finally still works.
                    jdbc.getConnection().commit();
                } catch (final SQLException e) {
                    throw new IllegalStateException("API v3 transaction commit failed", e);
                }
                jdbc.dispose();
            }
        };
    }
}
