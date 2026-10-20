package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.processor.channel.ChatroomChannel;
import org.b3log.symphony.util.ChatTopicValue;
import org.json.JSONObject;

/** Projection of committed state for old pages and legacy notifications; recoverable by the background worker. */
@Service
public class ChatTopicDeliveryService {
    @Inject private ChatTopicMgmtService topics;
    private ChatTopicValue applied;
    /** Re-read after commit; never apply a delayed callback's older topic over a newer committed one. */
    public synchronized void refreshCommitted()throws RepositoryException {
        final ChatTopicValue current=topics.readCommitted();
        if(current==null)return;
        if(applied!=null){
            if(current.revision()<applied.revision())throw new IllegalStateException("chat topic revision regressed");
            if(current.revision()==applied.revision()){
                if(!current.equals(applied))throw new IllegalStateException("chat topic changed without a revision");
                return;
            }
        }
        ChatroomChannel.discussing=current.text();
        applied=current;
        // Committed canonical state remains readable even if best-effort legacy delivery fails.
        publishLegacy(current);
    }
    protected void publishLegacy(final ChatTopicValue current){
        ChatroomChannel.notifyChat(new JSONObject().put("type","discussChanged").put("whoChanged",current.whoChanged())
                .put("newDiscuss",current.text()).put("topicRevision",Long.toString(current.revision())));
    }
}
