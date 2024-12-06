package org.b3log.symphony.util;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class NodeUtil {

    public static List<WebSocket> wsNodes = new ArrayList<>();

    public static void init() {
        wsNodes = new ArrayList<>();
        String[] nodes = Symphonys.get("chatroom.node.url").split(",");
        for (String i : nodes) {
            try {
                String serverUri = i + "?apiKey=" + Symphonys.get("chatroom.node.adminKey");
                SSLContext sslContext = createInsecureSSLContext();
                HttpClient client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();
                WebSocket webSocket = client.newWebSocketBuilder()
                        .buildAsync(URI.create(serverUri), new WebSocketListener())
                        .join();
                wsNodes.add(webSocket);
            } catch (Exception ignored) {
            }
        }
    }

    public static void notice(String text) {
        for (WebSocket i : wsNodes) {
            i.sendText(Symphonys.get("chatroom.node.adminKey") + ":::" + text, true);
        }
    }

    public static void sendTell(String who, String text) {
        notice("tell " + who + " " + text);
    }

    public static void sendMsg(String who, String text) {
        notice("msg " + who + " " + text);
    }

    public static void sendAll(String text) {
        notice("all " + text);
    }

    public static void sendSlow(String text) {
        notice("slow " + text);
    }

    public static void getOnline() {

    }

    // 创建忽略 SSL 证书的 SSLContext
    private static SSLContext createInsecureSSLContext() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public void checkClientTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(X509Certificate[] chain, String authType) {
                    }

                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
        return sslContext;
    }

    // 自定义 WebSocket 监听器
    private static class WebSocketListener implements WebSocket.Listener {

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1); // 请求接收一条消息
        }

        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            webSocket.request(1); // 请求接收下一条消息
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            return CompletableFuture.completedFuture(null);
        }
    }
}
