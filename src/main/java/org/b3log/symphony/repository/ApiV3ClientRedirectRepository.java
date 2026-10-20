package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3ClientRedirect;
import org.b3log.symphony.util.ApiV3TokenUtil;
import org.json.JSONObject;

@Repository
public class ApiV3ClientRedirectRepository extends AbstractRepository {
    public ApiV3ClientRedirectRepository(){super(ApiV3ClientRedirect.TABLE);}
    public static String identity(final String client,final String uri)throws Exception{return ApiV3TokenUtil.hash("oauth-redirect\0"+client+"\0"+uri);}
    public JSONObject getRegistered(final String client,final String uri)throws Exception {
        return read(client,uri,false);
    }
    public JSONObject getRegisteredForUpdate(final String client,final String uri)throws Exception {
        if(!hasTransactionBegun())throw new IllegalStateException("registered redirect transaction required");
        return read(client,uri,true);
    }
    private JSONObject read(final String client,final String uri,final boolean lock)throws Exception {
        final String key=identity(client,uri);final var rows=select("SELECT * FROM `"+getName()+"` WHERE oId=?"+(lock?" FOR UPDATE":""),key);
        if(rows.size()>1)throw new RepositoryException("duplicate client redirect");
        if(rows.isEmpty())return null;final JSONObject row=rows.getFirst();
        // SQL collations may fold case; the registered identity and complete URI must still match exactly.
        return key.equals(row.optString("oId"))&&client.equals(row.optString(ApiV3ClientRedirect.CLIENT_ID))
                &&uri.equals(row.optString(ApiV3ClientRedirect.REDIRECT_URI))
                &&row.optInt(ApiV3ClientRedirect.STATUS,-1)==ApiV3ClientRedirect.ACTIVE?row:null;
    }
}
