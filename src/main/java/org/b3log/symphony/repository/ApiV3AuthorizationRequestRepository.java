package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3AuthorizationRequest;
import org.json.JSONObject;
import org.b3log.symphony.util.ApiV3TransactionStatements;

@Repository
public class ApiV3AuthorizationRequestRepository extends AbstractRepository {
    public ApiV3AuthorizationRequestRepository(){super(ApiV3AuthorizationRequest.TABLE);}
    public int invalidatePendingForUserInCurrentTransaction(String userId)throws RepositoryException {
        if(!hasTransactionBegun())throw new IllegalStateException("consent transaction required");
        if(userId==null||!userId.matches("[1-9][0-9]{0,18}"))throw new IllegalArgumentException("account identity");
        return ApiV3TransactionStatements.execute("DELETE FROM `"+getName()+"` WHERE userId=? AND decision=?",userId,ApiV3AuthorizationRequest.PENDING);
    }
    /** A projection only, so the decision root can acquire its client lock before locking the request. */
    public JSONObject hintForOwner(final String hash,final String actor)throws RepositoryException {
        return one("SELECT oId,clientId,redirectUri,scope FROM `"+getName()+"` WHERE oId=? AND userId=?",hash,actor);
    }
    public JSONObject getForOwnerForUpdate(final String hash,final String actor)throws RepositoryException {
        if(!hasTransactionBegun())throw new IllegalStateException("consent transaction required");
        return one("SELECT * FROM `"+getName()+"` WHERE oId=? AND userId=? FOR UPDATE",hash,actor);
    }
    private JSONObject one(String sql,String hash,String actor)throws RepositoryException {
        final var rows=select(sql,hash,actor);if(rows.size()>1)throw new RepositoryException("duplicate authorization request");
        return rows.isEmpty()?null:rows.getFirst();
    }
}
