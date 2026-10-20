package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Ids;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.*;
import org.b3log.symphony.util.*;
import org.json.JSONObject;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/** Message, unread record and old list projection in the caller's one transaction. */
@Service
public class PrivateMessagePublicationService {
    @Inject private UserRepository users;
    @Inject private PrivateMessageRevisionService revisions;
    @Inject private ChatInfoRepository messages;
    @Inject private ChatUnreadRepository unread;
    @Inject private PrivateMessageListStateService lists;
    protected String newId(){return Ids.genTimeMillisId();}
    protected String time(){return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));}
    public JSONObject publishInCurrentTransaction(String actor,String peer,String content)throws Exception {
        PrivateMessageListStateService.session(actor,peer);validate(content);
        if(!messages.hasTransactionBegun())throw new IllegalStateException("private publication transaction required");
        return ApiV3TransactionSavepoint.run(()->{
            final var owners=users.select("SELECT oId,userStatus FROM `"+users.getName()+"` WHERE oId IN (?,?) ORDER BY oId FOR UPDATE",actor,peer);
            boolean sender=false,recipient=ApiV3PrivateMessages.FILE_TRANSFER_ID.equals(peer);
            for(var row:owners)if(row.optInt(UserExt.USER_STATUS,-1)==UserExt.USER_STATUS_C_VALID){if(actor.equals(row.getString("oId")))sender=true;if(peer.equals(row.getString("oId")))recipient=true;}
            if(!sender||!recipient)throw ApiV3Requests.notFound();
            final String id=newId(),unreadId=newId(),session=PrivateMessageListStateService.session(actor,peer),time=time();
            ApiV3PrivateMessages.requireId(id);ApiV3PrivateMessages.requireId(unreadId);
            ApiV3TransactionStatements.execute("INSERT INTO `"+messages.getName()+"` (oId,fromId,toId,user_session,time,content) VALUES(?,?,?,?,?,?)",id,actor,peer,session,time,content);
            ApiV3TransactionStatements.execute("INSERT INTO `"+unread.getName()+"` (oId,fromId,toId,user_session,messageId) VALUES(?,?,?,?,?)",unreadId,actor,peer,session,id);
            lists.refreshInCurrentTransaction(actor,peer);
            revisions.touchInCurrentTransaction(java.util.Set.of(actor,peer));
            return new JSONObject().put("id",id).put("peerId",peer).put("sentAt",time);
        });
    }
    public static void validate(String content){
        if(content==null||content.isBlank()||content.length()>10240||content.indexOf(0)>=0)throw ApiV3WriteInputs.invalid();
        try{ApiV3Json.canonical(content);}catch(IllegalArgumentException e){throw ApiV3WriteInputs.invalid();}
    }
}
