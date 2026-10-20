package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3AuthorizationCode;
import org.json.JSONObject;
import org.b3log.symphony.util.ApiV3TransactionStatements;

@Repository
public class ApiV3AuthorizationCodeRepository extends AbstractRepository {
    public ApiV3AuthorizationCodeRepository(){super(ApiV3AuthorizationCode.TABLE);}
    public int invalidatePendingForUserInCurrentTransaction(String userId)throws RepositoryException {
        if(!hasTransactionBegun())throw new IllegalStateException("authorization code transaction required");
        if(userId==null||!userId.matches("[1-9][0-9]{0,18}"))throw new IllegalArgumentException("account identity");
        return ApiV3TransactionStatements.execute("DELETE FROM `"+getName()+"` WHERE userId=? AND consumedAt=0",userId);
    }
    public JSONObject getForUpdate(final String hash)throws RepositoryException {
        if(!hasTransactionBegun())throw new IllegalStateException("authorization code transaction required");
        final var rows=select("SELECT * FROM `"+getName()+"` WHERE oId=? FOR UPDATE",hash);
        if(rows.size()>1)throw new RepositoryException("duplicate authorization code");
        return rows.isEmpty()?null:rows.getFirst();
    }
}
