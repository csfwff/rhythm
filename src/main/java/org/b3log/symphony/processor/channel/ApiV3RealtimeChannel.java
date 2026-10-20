package org.b3log.symphony.processor.channel;

import org.b3log.latke.Latkes;
import org.b3log.latke.http.WebSocketChannel;
import org.b3log.latke.http.WebSocketSession;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.service.ApiV3RealtimeService;
import org.b3log.symphony.util.ApiV3SocketHandshake;
import org.b3log.symphony.util.ApiV3Transactions;

@Singleton
public class ApiV3RealtimeChannel implements WebSocketChannel {
    @Inject private ApiV3RealtimeService realtime;
    public static String path(){return Latkes.getContextPath()+"/api/v3/realtime";}
    @Override public void onConnect(WebSocketSession session){
        final var peer=ApiV3SocketHandshake.inspect(session,path());
        if(peer==null||!ApiV3SocketHandshake.prepare(session)){session.close();return;}realtime.open(session,peer.ip());
    }
    @Override public void onMessage(Message message){
        try{realtime.receive(message.session,message.text);}
        finally{try{ApiV3Transactions.releaseThreadResources();}catch(RuntimeException ignored){realtime.close(message.session);}}
    }
    @Override public void onClose(WebSocketSession session){realtime.close(session);}
    @Override public void onError(Error error){realtime.close(error.session);}
}
