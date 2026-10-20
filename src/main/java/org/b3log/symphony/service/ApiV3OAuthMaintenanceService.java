package org.b3log.symphony.service;

import org.apache.logging.log4j.LogManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.ApiV3Token;
import org.b3log.symphony.repository.*;
import org.b3log.symphony.util.ApiV3Transactions;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/** Bounded cleanup. Used code records remain while any unexpired active member of their token family exists. */
@Service
public class ApiV3OAuthMaintenanceService {
    @Inject private ApiV3AuthorizationRequestRepository requests;
    @Inject private ApiV3AuthorizationCodeRepository codes;
    @Inject private ApiV3TokenRepository tokens;
    private boolean started;
    protected long now(){return System.currentTimeMillis();}
    public synchronized void start(){
        if(started)return;started=true;
        Executors.newSingleThreadScheduledExecutor(Thread.ofPlatform().daemon().name("v3-oauth-cleanup").factory()).scheduleWithFixedDelay(()->{
            try{cleanup();}catch(Exception e){LogManager.getLogger(getClass()).warn("OAuth cleanup unavailable");}
            finally{try{ApiV3Transactions.releaseThreadResources();}catch(RuntimeException e){LogManager.getLogger(getClass()).warn("OAuth cleanup JDBC release failed");}}
        },1,1,TimeUnit.MINUTES);
    }
    public int cleanup()throws Exception {
        if(codes.hasTransactionBegun())throw new IllegalStateException("OAuth cleanup root already active");
        final long time=now(),cutoff=time-TimeUnit.DAYS.toMillis(1);int removed=0;
        final Transaction tx=ApiV3Transactions.strict(codes.beginTransaction());try{
            for(var row:requests.select("SELECT oId FROM `"+requests.getName()+"` WHERE expiresAt<=? ORDER BY expiresAt LIMIT ? FOR UPDATE",cutoff,200)){requests.remove(row.getString("oId"));removed++;}
            final String sql="SELECT c.oId FROM `"+codes.getName()+"` c WHERE c.expiresAt<=? AND (c.consumedAt=0 OR NOT EXISTS (SELECT 1 FROM `"+tokens.getName()+"` t WHERE t.clientId=c.clientId AND t.userId=c.userId AND t.familyId=c.familyId AND t.state=? AND t.expiresAt>?)) ORDER BY c.expiresAt LIMIT ? FOR UPDATE";
            for(var row:codes.select(sql,cutoff,ApiV3Token.STATE_ACTIVE,time,200)){codes.remove(row.getString("oId"));removed++;}
            tx.commit();return removed;
        }finally{if(tx.isActive())tx.rollback();}
    }
}
