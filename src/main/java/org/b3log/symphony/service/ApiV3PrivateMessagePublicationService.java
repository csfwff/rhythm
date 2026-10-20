package org.b3log.symphony.service;

import org.apache.logging.log4j.LogManager;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.processor.ChatProcessor;
import org.b3log.symphony.util.*;
import org.json.JSONObject;

@Service
public class ApiV3PrivateMessagePublicationService {
    private static final org.apache.logging.log4j.Logger LOGGER=LogManager.getLogger(ApiV3PrivateMessagePublicationService.class);
    @Inject private PrivateMessagePublicationService publication;
    @Inject private ApiV3IdempotencyMgmtService idempotency;
    protected String prepare(String content){return ReservedWords.processReservedWord(content).trim();}
    /** Cache eviction is safe on replay and repairs a lost successful commit acknowledgement. */
    protected void afterPublished(String actor,String peer){ChatProcessor.invalidateChatListCache(actor,peer);}
    public JSONObject send(JSONObject viewer,String peer,String content,String key)throws Exception {
        final String actor=viewer==null?"":viewer.optString(Keys.OBJECT_ID);
        PrivateMessageListStateService.session(actor,peer);PrivateMessagePublicationService.validate(content);
        try{
            final var result=idempotency.execute(actor,"private.send:"+peer,key,new JSONObject().put("content",content),
                    ()->publication.publishInCurrentTransaction(actor,peer,prepare(content)));
            try{afterPublished(actor,peer);}catch(Exception e){LOGGER.warn("Private publication refresh unavailable");}
            return result;
        }catch(ApiV3Exception e){throw e;}
        catch(Exception e){LOGGER.warn("Private publication unavailable");throw new ApiV3Exception(503,"temporarily_unavailable","私信暂不可用");}
    }
}
