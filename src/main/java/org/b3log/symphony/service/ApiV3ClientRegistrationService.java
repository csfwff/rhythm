package org.b3log.symphony.service;

import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Ids;
import org.b3log.symphony.model.ApiV3Client;
import org.b3log.symphony.model.ApiV3ClientRedirect;
import org.b3log.symphony.repository.ApiV3ClientRepository;
import org.b3log.symphony.repository.ApiV3ClientRedirectRepository;
import org.b3log.symphony.util.ApiV3Json;
import org.b3log.symphony.util.ApiV3OAuthRedirects;
import org.b3log.symphony.util.ApiV3TokenUtil;
import org.b3log.symphony.util.ApiV3TransactionSavepoint;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.json.JSONObject;

/** Registration primitive for an already approved application, never a public self-registration grant. */
@Service
public class ApiV3ClientRegistrationService {
    @Inject private ApiV3ClientRepository clients;
    @Inject private ApiV3ClientRedirectRepository redirects;
    protected boolean development(){return Latkes.RuntimeMode.DEVELOPMENT==Latkes.getRuntimeMode();}
    protected String rowId(){return Ids.genTimeMillisId();}
    protected String clientId(){return "app_"+ApiV3TokenUtil.generate();}

    /** Only public registration fields. The ID is generated here, never selected by a requester. */
    public record Registered(String clientId,String name,String scope,List<String> redirectUris){
        public Registered{redirectUris=List.copyOf(redirectUris);}
    }

    /** Pure normalization shared by approved creation and administrative full replacement. */
    public record Definition(String name,String scope,List<String> redirectUris){
        public Definition{redirectUris=List.copyOf(redirectUris);}
    }
    public Definition normalizeDefinition(String name,String scope,List<String> redirectUris){
        final String display=displayName(name),permissions=permissions(scope);
        if(redirectUris==null||redirectUris.isEmpty())throw new IllegalArgumentException("registered redirect required");
        final List<String> callbacks=List.copyOf(redirectUris);
        for(String uri:callbacks)if(!ApiV3OAuthRedirects.valid(uri,development()))throw new IllegalArgumentException("invalid registered redirect");
        if(Set.copyOf(callbacks).size()!=callbacks.size())throw new IllegalArgumentException("duplicate registered redirect");
        return new Definition(display,permissions,callbacks);
    }

    /** The caller owns approval, authentication, idempotency and the strict root commit. */
    public Registered createApprovedInCurrentTransaction(String name,String scope,List<String> redirectUris)throws Exception {
        if(!clients.hasTransactionBegun()||!redirects.hasTransactionBegun())throw new IllegalStateException("registration owner transaction required");
        final Definition definition=normalizeDefinition(name,scope,redirectUris);
        final String display=definition.name(),permissions=definition.scope();
        final List<String> callbacks=definition.redirectUris();
        return ApiV3TransactionSavepoint.run(()->{
            final String id=clientId(),row=rowId();
            if(id==null||!id.matches("app_[A-Za-z0-9_-]{43}")||row==null||!row.matches("[1-9][0-9]{0,18}"))throw new IllegalStateException("invalid registration identity");
            final JSONObject value=new JSONObject().put(Keys.OBJECT_ID,row).put(ApiV3Client.CLIENT_ID,id)
                    .put(ApiV3Client.NAME,display).put(ApiV3Client.SCOPE,permissions).put(ApiV3Client.STATUS,ApiV3Client.STATUS_ACTIVE);
            if(!row.equals(clients.add(value)))throw new IllegalStateException("client identity changed");
            for(String uri:callbacks){
                final String key=ApiV3ClientRedirectRepository.identity(id,uri);
                final JSONObject redirect=new JSONObject().put(Keys.OBJECT_ID,key).put(ApiV3ClientRedirect.CLIENT_ID,id)
                        .put(ApiV3ClientRedirect.REDIRECT_URI,uri).put(ApiV3ClientRedirect.STATUS,ApiV3ClientRedirect.ACTIVE);
                if(!key.equals(redirects.add(redirect)))throw new IllegalStateException("redirect identity changed");
            }
            return new Registered(id,display,permissions,callbacks);
        });
    }
    private static String displayName(String value){
        if(value==null)throw new IllegalArgumentException("client name required");
        final String name=value.strip();
        if(name.isEmpty()||name.length()>128||name.codePoints().anyMatch(Character::isISOControl))throw new IllegalArgumentException("invalid client name");
        ApiV3Json.canonical(name);return name;
    }
    private static String permissions(String value){
        if(value==null||value.isEmpty()||value.length()>255)throw new IllegalArgumentException("client scope required");
        final Set<String> available=Set.copyOf(Arrays.asList(ApiV3TokenMgmtService.DEFAULT_SCOPES.split(" ")));
        final TreeSet<String> scopes=new TreeSet<>();
        for(String item:value.split(" ",-1)){
            if(!available.contains(item))throw new IllegalArgumentException("unknown client scope");scopes.add(item);
        }
        return String.join(" ",scopes);
    }
}
