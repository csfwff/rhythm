package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.ChatUnreadRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.*;

/** Owner-only state mutation. A fixed upper ID preserves arrivals after this operation's cutoff. */
@Service
public class PrivateMessageReadStateService {
    @Inject private UserRepository users;
    @Inject private PrivateMessageRevisionService revisions;
    @Inject private ChatUnreadRepository unread;

    public long markInCurrentTransaction(String actor,String peer)throws Exception {
        ApiV3PrivateMessages.requireId(actor);
        if(peer!=null){ApiV3PrivateMessages.requireId(peer);if(actor.equals(peer))throw ApiV3WriteInputs.invalid();}
        if(!unread.hasTransactionBegun())throw new IllegalStateException("private read state transaction required");
        final var owners=users.select("SELECT oId,userStatus FROM `"+users.getName()+"` WHERE oId=? FOR UPDATE",actor);
        if(owners.size()!=1||owners.getFirst().optInt(UserExt.USER_STATUS,-1)!=UserExt.USER_STATUS_C_VALID)throw ApiV3Requests.notFound();
        final String where="toId=? AND fromId REGEXP '"+ApiV3PrivateMessages.ID_PATTERN+"' AND fromId<>toId"+(peer==null?"":" AND fromId=?");
        final Object[] args=peer==null?new Object[]{actor}:new Object[]{actor,peer};
        final var rows=unread.select("SELECT oId FROM `"+unread.getName()+"` WHERE "+where+" ORDER BY oId DESC LIMIT 1 FOR UPDATE",args);
        if(rows.isEmpty())return 0;
        final String cutoff=rows.getFirst().getString("oId");
        if(!ApiV3PrivateMessages.validId(cutoff))throw new IllegalStateException("invalid unread cutoff");
        final var values=new java.util.ArrayList<Object>(java.util.Arrays.asList(args));values.add(cutoff);
        return ApiV3TransactionSavepoint.run(()->{
            final long changed=ApiV3TransactionStatements.execute("DELETE FROM `"+unread.getName()+"` WHERE "+where+" AND oId<=?",values.toArray());
            if(changed>0)revisions.touchInCurrentTransaction(java.util.Set.of(actor));
            return changed;
        });
    }
}
