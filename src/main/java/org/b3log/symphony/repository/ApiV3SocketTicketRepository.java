package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3SocketTicket;
import org.b3log.symphony.util.ApiV3Transactions;
import org.json.JSONObject;

@Repository
public class ApiV3SocketTicketRepository extends AbstractRepository {
    public ApiV3SocketTicketRepository() { super(ApiV3SocketTicket.REPOSITORY); }
    @Override public Transaction beginTransaction() { return ApiV3Transactions.strict(super.beginTransaction()); }
    public JSONObject getForUpdate(final String hash) throws RepositoryException {
        if (!hasTransactionBegun()) throw new IllegalStateException("socket ticket transaction required");
        final var rows = select("SELECT * FROM `" + getName() + "` WHERE oId=? FOR UPDATE", hash);
        return rows.isEmpty() ? null : rows.getFirst();
    }
    public java.util.List<JSONObject> expiredBatchForUpdate(final long cutoff) throws RepositoryException {
        if (!hasTransactionBegun()) throw new IllegalStateException("socket ticket transaction required");
        return select("SELECT oId FROM `" + getName() + "` WHERE expiresAt<? ORDER BY expiresAt,oId LIMIT 200 FOR UPDATE",cutoff);
    }
}
