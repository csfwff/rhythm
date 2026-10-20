package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.repository.ApiV3PrivateRevisionRepository;
import org.b3log.symphony.util.ApiV3PrivateMessages;
import java.util.*;

/** Caller owns the transaction and savepoint containing both data and these invalidation markers. */
@Service
public class PrivateMessageRevisionService {
    @Inject private ApiV3PrivateRevisionRepository revisions;
    public void touchInCurrentTransaction(Collection<String> owners)throws Exception {
        if(!revisions.hasTransactionBegun())throw new IllegalStateException("private revision transaction required");
        final var ids=new TreeSet<String>();
        for(String owner:owners){ApiV3PrivateMessages.requireId(owner);if(!ApiV3PrivateMessages.FILE_TRANSFER_ID.equals(owner))ids.add(owner);}
        for(String owner:ids)revisions.touchInCurrentTransaction(owner);
    }
}
