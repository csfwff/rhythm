package org.b3log.symphony.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.model.User;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.channel.ChatroomChannel;
import org.b3log.symphony.service.AvatarQueryService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

public class NodeUtil {

    public static List<String> uriNodes = new ArrayList<>();

    public static List<WebSocket> wsNodes = new ArrayList<>();

    public static Map<String, Integer> wsOnline = new HashMap<>();

    private static final Logger LOGGER = LogManager.getLogger(NodeUtil.class);

    public static JSONArray remoteUsers = new JSONArray();

    public static void init() {
        LOGGER.log(Level.INFO, "Loading nodes");
        wsNodes = new ArrayList<>();
        uriNodes = new ArrayList<>();
        String[] nodes = Symphonys.get("chatroom.node.url").split(",");
        for (String i : nodes) {
            String serverUri = i + "?apiKey=" + Symphonys.get("chatroom.node.adminKey");
            try {
                SSLContext sslContext = createInsecureSSLContext();
                HttpClient client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();

                CompletableFuture<String> future = new CompletableFuture<>();
                WebSocket webSocket = client.newWebSocketBuilder()
                        .buildAsync(URI.create(serverUri), new WebSocketListener(future))
                        .join();

                webSocket.sendText(Symphonys.get("chatroom.node.adminKey") + ":::hello", true);
                future.get(10, TimeUnit.SECONDS);
                wsNodes.add(webSocket);
                uriNodes.add(i);
                LOGGER.log(Level.INFO, "Connected to node: " + serverUri);
            } catch (Exception e) {
                LOGGER.log(Level.ERROR, "Failed to connect to node: " + serverUri, e);
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

    public static void initOnline() {
        JSONArray onlineList = new JSONArray();
        wsOnline = new HashMap<>();
        for (String i : uriNodes) {
            try {
                String serverUri = i + "?apiKey=" + Symphonys.get("chatroom.node.adminKey");
                SSLContext sslContext = createInsecureSSLContext();
                HttpClient client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();
                CompletableFuture<String> responseFuture = new CompletableFuture<>();
                WebSocket webSocket = client.newWebSocketBuilder()
                        .buildAsync(URI.create(serverUri), new WebSocketListener(responseFuture))
                        .join();
                webSocket.sendText(Symphonys.get("chatroom.node.adminKey") + ":::online", true);
                try {
                    String response = responseFuture.get(10, TimeUnit.SECONDS);
                    JSONArray jsonArray = new JSONArray(response);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        onlineList.put(jsonArray.get(j));
                    }
                    wsOnline.put(i, jsonArray.length());
                    webSocket.sendClose(1000, "Nornal Closure");
                    LOGGER.log(Level.INFO, "Remote " + i + " online list updated. count=" + jsonArray.length());
                } catch (Exception e) {
                    webSocket.sendClose(1000, "Nornal Closure");
                    LOGGER.log(Level.ERROR, serverUri + " No response within 10 seconds. giveup.", e);
                }
            } catch (Exception ignored) {
            }
        }
        remoteUsers = onlineList;

        // 推送在线信息给子节点
        final BeanManager beanManager = BeanManager.getInstance();
        final AvatarQueryService avatarQueryService = beanManager.getReference(AvatarQueryService.class);

        Map<String, JSONObject> filteredOnlineUsers = new HashMap<>();
        for (JSONObject object : ChatroomChannel.onlineUsers.values()) {
            String name = object.optString(User.USER_NAME);
            filteredOnlineUsers.put(name, object);
        }

        for (int i = 0; i < NodeUtil.remoteUsers.length(); i++) {
            try {
                JSONObject temp = NodeUtil.remoteUsers.getJSONObject(i);
                filteredOnlineUsers.put(temp.optString(User.USER_NAME), temp);
            } catch (Exception ignored) {
            }
        }

        JSONArray onlineArray = new JSONArray();
        for (String user : filteredOnlineUsers.keySet()) {
            JSONObject object = filteredOnlineUsers.get(user);

            String avatar = object.optString(UserExt.USER_AVATAR_URL);
            String homePage = Latkes.getStaticServePath() + "/member/" + user;

            JSONObject generated = new JSONObject();
            generated.put(User.USER_NAME, user);
            generated.put(UserExt.USER_AVATAR_URL, avatar);
            avatarQueryService.fillUserAvatarURL(generated);
            generated.put("homePage", homePage);
            onlineArray.put(generated);
        }

        JSONObject result = new JSONObject();
        result.put(Common.ONLINE_CHAT_CNT, filteredOnlineUsers.size());
        result.put(Common.TYPE, "online");
        result.put("users", onlineArray);
        result.put("discussing", ChatroomChannel.discussing);

        for (String i : uriNodes) {
            try {
                String serverUri = i + "?apiKey=" + Symphonys.get("chatroom.node.adminKey");
                SSLContext sslContext = createInsecureSSLContext();
                HttpClient client = HttpClient.newBuilder()
                        .sslContext(sslContext)
                        .build();
                CompletableFuture<String> responseFuture = new CompletableFuture<>();
                WebSocket webSocket = client.newWebSocketBuilder()
                        .buildAsync(URI.create(serverUri), new WebSocketListener(responseFuture))
                        .join();
                webSocket.sendText(Symphonys.get("chatroom.node.adminKey") + ":::push " + result, true);
                try {
                    String response = responseFuture.get(10, TimeUnit.SECONDS);
                    webSocket.sendClose(1000, "Nornal Closure");
                    LOGGER.log(Level.INFO, "Remote " + i + " has received the full online list.");
                } catch (Exception e) {
                    webSocket.sendClose(1000, "Nornal Closure");
                    LOGGER.log(Level.ERROR, "Push online list to " + serverUri + " has no response within 10 seconds. giveup.", e);
                }
            } catch (Exception ignored) {
            }
        }
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

    private static class WebSocketListener implements WebSocket.Listener {
        private final CompletableFuture<String> responseFuture;

        public WebSocketListener(CompletableFuture<String> responseFuture) {
            this.responseFuture = responseFuture;
        }

        @Override
        public void onOpen(WebSocket webSocket) {
            webSocket.request(1); // 请求接收一条消息
        }

        private final StringBuilder messageBuffer = new StringBuilder();
        @Override
        public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
            messageBuffer.append(data);

            if (last) {
                String completeMessage = messageBuffer.toString();
                messageBuffer.setLength(0);

                if (!responseFuture.isDone()) {
                    responseFuture.complete(completeMessage);
                }
            }

            webSocket.request(1);
            return CompletableFuture.completedFuture(null);
        }

        @Override
        public void onError(WebSocket webSocket, Throwable error) {
            responseFuture.completeExceptionally(error); // 异常时完成 Future
        }

        @Override
        public CompletionStage<?> onClose(WebSocket webSocket, int statusCode, String reason) {
            return CompletableFuture.completedFuture(null);
        }
    }

    private static class WebSocketListenerNoFuture implements WebSocket.Listener {
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
