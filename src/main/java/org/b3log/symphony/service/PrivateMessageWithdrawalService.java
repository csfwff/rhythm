package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.*;
import org.b3log.symphony.util.*;
import org.json.JSONObject;

/** Only the sender may remove a message. Unknown historical unread associations are preserved. */
@Service
public class PrivateMessageWithdrawalService {
    @Inject private UserRepository users;
    @Inject private PrivateMessageRevisionService revisions;
    @Inject private ChatInfoRepository messages;
    @Inject private ChatUnreadRepository unread;
    @Inject private PrivateMessageListStateService lists;

    public JSONObject removeInCurrentTransaction(String actor,String id)throws Exception {
        ApiV3PrivateMessages.requireId(actor);ApiV3PrivateMessages.requireId(id);
        if(!messages.hasTransactionBegun())throw new IllegalStateException("private withdrawal transaction required");
        return ApiV3TransactionSavepoint.run(()->{
            // An owner-filtered hint supplies the complete user lock set before locking a message.
            final var hint=messages.select("SELECT toId FROM `"+messages.getName()+"` WHERE oId=? AND fromId=?",id,actor);
            if(hint.size()!=1)throw ApiV3Requests.notFound();
            final String peer=hint.getFirst().optString("toId");
            if(!ApiV3PrivateMessages.validId(peer)||actor.equals(peer))throw ApiV3Requests.notFound();
            final var owners=users.select("SELECT oId,userStatus FROM `"+users.getName()+"` WHERE oId IN (?,?) ORDER BY oId FOR UPDATE",actor,peer);
            if(owners.stream().noneMatch(row->actor.equals(row.optString("oId"))&&row.optInt(UserExt.USER_STATUS,-1)==UserExt.USER_STATUS_C_VALID))throw ApiV3Requests.notFound();
            final var current=messages.select("SELECT toId FROM `"+messages.getName()+"` WHERE oId=? AND fromId=? FOR UPDATE",id,actor);
            if(current.size()!=1)throw ApiV3Requests.notFound();
            if(!peer.equals(current.getFirst().optString("toId")))throw new ApiV3Exception(409,"message_changed","消息状态已变化，请重试");
            if(ApiV3TransactionStatements.execute("DELETE FROM `"+messages.getName()+"` WHERE oId=? AND fromId=? AND toId=?",id,actor,peer)!=1)throw ApiV3Requests.notFound();
            ApiV3TransactionStatements.execute("DELETE FROM `"+unread.getName()+"` WHERE messageId=? AND fromId=? AND toId=?",id,actor,peer);
            lists.refreshInCurrentTransaction(actor,peer);
            revisions.touchInCurrentTransaction(java.util.Set.of(actor,peer));
            return new JSONObject().put("id",id).put("peerId",peer).put("removed",true);
        });
    }
    /** Strict root used by the approved legacy GET /chat/revoke adapter. */
    public JSONObject removeLegacy(String actor,String id)throws Exception {
        if(messages.hasTransactionBegun())throw new IllegalStateException("private withdrawal root required");
        final var tx=ApiV3Transactions.strict(messages.beginTransaction());
        try{final var result=removeInCurrentTransaction(actor,id);tx.commit();return result;}
        finally{if(tx.isActive())tx.rollback();}
    }
}
