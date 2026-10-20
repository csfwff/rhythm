package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3Request;
import org.b3log.symphony.util.ApiV3MutationStore;
import org.b3log.symphony.util.ApiV3Transactions;
import org.json.JSONObject;

import java.util.List;

@Repository
public class ApiV3RequestRepository extends AbstractRepository implements ApiV3MutationStore {
    public ApiV3RequestRepository() { super(ApiV3Request.API_V3_REQUEST); }
    @Override public Transaction beginTransaction() { return ApiV3Transactions.strict(super.beginTransaction()); }

    public JSONObject getForUpdate(final String id) throws RepositoryException {
        final List<JSONObject> rows = select("SELECT * FROM " + getName() + " WHERE oId=? FOR UPDATE", id);
        return rows.isEmpty() ? null : rows.get(0);
    }
}
