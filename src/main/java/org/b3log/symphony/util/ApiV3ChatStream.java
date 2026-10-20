package org.b3log.symphony.util;

import org.b3log.symphony.model.ApiV3ChatEvent;
import org.json.JSONObject;
import java.util.List;

/** Per-backend committed event cursor; gaps require HTTP synchronization, never silent skipping. */
public final class ApiV3ChatStream {
    public interface Source {long head()throws Exception;List<JSONObject> after(long revision,long through,int limit)throws Exception;}
    public interface Sink {void changed(String kind,String messageId,long revision);void syncRequired();}
    private final Source source;private final Sink sink;private Long cursor;
    public ApiV3ChatStream(Source source,Sink sink){this.source=source;this.sink=sink;}
    public synchronized void reset(){cursor=null;}
    public synchronized void poll()throws Exception {
        final long head=source.head();if(head<0)throw new IllegalStateException("invalid chat head");
        if(cursor==null||head<cursor){probe(head);sink.syncRequired();cursor=head;return;}
        if(head==cursor){probe(head);return;}
        for(int batch=0;cursor<head&&batch<5;batch++){
            final List<JSONObject> rows=source.after(cursor,head,200);
            if(rows.isEmpty()){sink.syncRequired();cursor=head;return;}
            if(rows.size()>200)throw new IllegalStateException("invalid chat event batch");
            for(final JSONObject row:rows){
                final long revision=row.getLong("revision");
                final String id=row.getString("messageId"),kind=row.getString("kind");
                if(revision<=cursor||revision>head||!ApiV3ChatEvent.validChange(kind,id))throw new IllegalStateException("invalid chat event");
                if(revision!=cursor+1){sink.syncRequired();cursor=head;return;}
                sink.changed(kind,id,revision);cursor=revision;
            }
        }
    }
    private void probe(long head)throws Exception {
        if(!source.after(head,head,200).isEmpty())throw new IllegalStateException("invalid chat event probe");
    }
}
