package org.b3log.symphony.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.processor.ChatProcessor;
import org.b3log.symphony.util.*;
import org.json.JSONObject;

@Service
public class ApiV3PrivateMessageWithdrawalService {
    private static final Logger LOGGER=LogManager.getLogger(ApiV3PrivateMessageWithdrawalService.class);
    @Inject private PrivateMessageWithdrawalService withdrawal;
    @Inject private ApiV3IdempotencyMgmtService idempotency;
    protected void afterRemoved(String actor,String peer){ChatProcessor.invalidateChatListCache(actor,peer);}
    public JSONObject remove(JSONObject viewer,String id,String key)throws Exception {
        final String actor=viewer==null?"":viewer.optString(Keys.OBJECT_ID);
        ApiV3PrivateMessages.requireId(actor);ApiV3PrivateMessages.requireId(id);
        try{
            final var result=idempotency.execute(actor,"private.withdraw:"+id,key,new JSONObject(),()->withdrawal.removeInCurrentTransaction(actor,id));
            // Safe on replay, including recovery from a successful commit whose acknowledgement was lost.
            try{afterRemoved(actor,result.getString("peerId"));}catch(Exception e){LOGGER.warn("Private withdrawal refresh unavailable");}
            return result;
        }catch(ApiV3Exception e){throw e;}
        catch(Exception e){LOGGER.warn("Private withdrawal unavailable");throw new ApiV3Exception(503,"temporarily_unavailable","私信暂不可用");}
    }
}
