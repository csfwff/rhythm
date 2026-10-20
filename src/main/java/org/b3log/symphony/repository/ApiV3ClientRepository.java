package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3Client;
import org.json.JSONObject;
import java.util.List;

@Repository
public class ApiV3ClientRepository extends AbstractRepository {
    public ApiV3ClientRepository() {
        super(ApiV3Client.API_V3_CLIENT);
    }

    public JSONObject getByClientId(final String clientId) throws RepositoryException {
        return getFirst(new Query().setFilter(new PropertyFilter(ApiV3Client.CLIENT_ID,
                FilterOperator.EQUAL, clientId)).setPageCount(1));
    }
    /** All token family writers acquire the client row before token rows, across backend instances. */
    public JSONObject getByClientIdForUpdate(final String clientId) throws RepositoryException {
        if (!hasTransactionBegun()) throw new IllegalStateException("client transaction required");
        final List<JSONObject> rows=select("SELECT * FROM `"+getName()+"` WHERE clientId=? FOR UPDATE",clientId);
        if(rows.size()>1)throw new RepositoryException("duplicate v3 client");
        return rows.isEmpty()?null:rows.getFirst();
    }
    /** Password reset precedes user/code/token locks with the same client locks as OAuth writers. */
    public void lockForAccountResetInCurrentTransaction()throws RepositoryException {
        if(!hasTransactionBegun())throw new IllegalStateException("client transaction required");
        select("SELECT clientId FROM `"+getName()+"` ORDER BY clientId FOR UPDATE");
    }
}
