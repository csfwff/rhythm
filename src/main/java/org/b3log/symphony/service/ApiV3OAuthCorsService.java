package org.b3log.symphony.service;

import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.*;
import org.b3log.symphony.repository.ApiV3ClientRepository;
import org.b3log.symphony.repository.ApiV3ClientRedirectRepository;
import org.b3log.symphony.util.*;

/** Credential-less browser access to OAuth token/revocation responses, only from current callback origins. */
@Service
public class ApiV3OAuthCorsService {
    @Inject private ApiV3ClientRepository clients;
    @Inject private ApiV3ClientRedirectRepository redirects;
    public boolean allowed(String client,String origin)throws Exception {
        if(origin==null)return true;
        if(client==null||client.isBlank()||client.length()>64||origin.length()>4096)return false;
        final var registration=clients.getByClientId(client);
        if(registration==null||!client.equals(registration.optString(ApiV3Client.CLIENT_ID))
                ||registration.optInt(ApiV3Client.STATUS,-1)!=ApiV3Client.STATUS_ACTIVE||ApiV3TokenMgmtService.WEBSITE_CLIENT.equals(client))return false;
        final boolean development=Latkes.RuntimeMode.DEVELOPMENT==Latkes.getRuntimeMode();
        for(var row:redirects.select("SELECT clientId,redirectUri FROM `"+redirects.getName()+"` WHERE clientId=? AND status=?",client,ApiV3ClientRedirect.ACTIVE)){
            final String uri=row.optString(ApiV3ClientRedirect.REDIRECT_URI);
            if(client.equals(row.optString(ApiV3ClientRedirect.CLIENT_ID))&&ApiV3OAuthRedirects.valid(uri,development)
                    &&ApiV3ConsentSecurity.sameOrigin(uri,origin,null,null))return true;
        }
        return false;
    }
}
