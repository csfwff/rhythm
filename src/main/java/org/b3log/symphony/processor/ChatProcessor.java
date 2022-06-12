package org.b3log.symphony.processor;

import org.apache.commons.lang.RandomStringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.util.Crypts;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.middleware.ApiCheckMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.ChatInfoRepository;
import org.b3log.symphony.repository.ChatUnreadRepository;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.UserQueryService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

@Singleton
public class ChatProcessor {

    @Inject
    private ChatUnreadRepository chatUnreadRepository;

    @Inject
    private ChatInfoRepository chatInfoRepository;

    @Inject
    private DataModelService dataModelService;

    @Inject
    private UserQueryService userQueryService;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final ApiCheckMidware apiCheck = beanManager.getReference(ApiCheckMidware.class);
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final ChatProcessor chatProcessor = beanManager.getReference(ChatProcessor.class);
        Dispatcher.get("/chat", chatProcessor::showChat, loginCheck::handle);
        Dispatcher.get("/chat/has-unread", chatProcessor::hasUnreadChatMessage, apiCheck::handle);
        Dispatcher.get("/chat/get-list", chatProcessor::getList, apiCheck::handle);
        Dispatcher.get("/chat/get-message", chatProcessor::getMessage, apiCheck::handle);

    }

    public void getMessage(final RequestContext context) {
        context.renderJSON(new JSONObject().put("result", 0));
        JSONObject currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        String userId = currentUser.optString(Keys.OBJECT_ID);
    }

    public void getList(final RequestContext context) {
        context.renderJSON(new JSONObject().put("result", 0));
        JSONObject currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        String userId = currentUser.optString(Keys.OBJECT_ID);
        try {
            List<JSONObject> chatUsersFrom = chatInfoRepository.select(
                    "SELECT DISTINCT toId " +
                            "FROM " + chatInfoRepository.getName() + " " +
                            "WHERE fromId = ?", userId);
            List<JSONObject> chatUsersTo = chatInfoRepository.select(
                    "SELECT DISTINCT fromId " +
                            "FROM " + chatInfoRepository.getName() + " " +
                            "WHERE toId = ?", userId);

            List<JSONObject> infoList = new LinkedList<>();
            // 加载我发送的
            for (JSONObject chatUser : chatUsersFrom) {
                String toId = chatUser.optString("toId");
                try {
                    List<JSONObject> chatInfo = chatInfoRepository.select(
                            "SELECT * FROM " + chatInfoRepository.getName() +
                                    " WHERE fromId = ? AND toId = ? ORDER BY oId DESC LIMIT 1",
                            userId, toId
                    );
                    if (chatInfo.size() > 0) {
                        JSONObject result = chatInfo.get(0);
                        infoList.add(result);
                    }
                } catch (Exception ignored) {
                }
            }
            // 加载发给我的（要比对时间去重）
            List<JSONObject> forMeList = new LinkedList<>();
            for (JSONObject chatUser : chatUsersTo) {
                String fromId = chatUser.optString("fromId");
                try {
                    List<JSONObject> chatInfo = chatInfoRepository.select(
                            "SELECT * FROM " + chatInfoRepository.getName() +
                                    " WHERE fromId = ? AND toId = ? ORDER BY oId DESC LIMIT 1",
                            fromId, userId
                    );
                    if (chatInfo.size() > 0) {
                        JSONObject result = chatInfo.get(0);
                        Iterator<JSONObject> iterator = infoList.iterator();
                        while (iterator.hasNext()) {
                            JSONObject info = iterator.next();
                            String iFromId = info.optString("fromId");
                            String iToId = info.optString("toId");
                            String rFromId = result.optString("fromId");
                            String rToId = result.optString("toId");
                            if (iFromId.equals(rToId) && iToId.equals(rFromId)) {
                                // 相同
                                long iOId = Long.parseLong(info.optString(Keys.OBJECT_ID));
                                long rOId = Long.parseLong(result.optString(Keys.OBJECT_ID));
                                if (rOId > iOId) {
                                    iterator.remove();
                                    forMeList.add(result);
                                }
                            } else {
                                forMeList.add(result);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            List<JSONObject> forMeListDistinct = forMeList.stream().distinct().collect(Collectors.toList());
            infoList.addAll(forMeListDistinct);
            // 渲染用户信息
            for (JSONObject info : infoList) {
                String fromId = info.optString("fromId");
                String toId = info.optString("toId");
                JSONObject senderJSON = userQueryService.getUser(fromId);
                if (!fromId.equals("1000000000086")) {
                    info.put("senderUserName", senderJSON.optString(User.USER_NAME));
                    info.put("senderAvatar", senderJSON.optString(UserExt.USER_AVATAR_URL));
                } else {
                    info.put("receiverUserName", "文件传输助手");
                    info.put("receiverAvatar", "https://file.fishpi.cn/2022/06/e1541bfe4138c144285f11ea858b6bf6-ba777366.jpeg");
                }
                JSONObject receiverJSON = userQueryService.getUser(toId);
                if (!toId.equals("1000000000086")) {
                    info.put("receiverUserName", receiverJSON.optString(User.USER_NAME));
                    info.put("receiverAvatar", receiverJSON.optString(UserExt.USER_AVATAR_URL));
                } else {
                    info.put("receiverUserName", "文件传输助手");
                    info.put("receiverAvatar", "https://file.fishpi.cn/2022/06/e1541bfe4138c144285f11ea858b6bf6-ba777366.jpeg");
                }
                // 将content过滤为纯文本
                String content = info.optString("content");
                String preview = content.replaceAll("[^a-zA-Z0-9\\u4E00-\\u9FA5]", "");
                info.put("preview", preview.length() > 20 ? preview.substring(0, 20) : preview);
            }
            context.renderJSON(new JSONObject().put("result", 0)
                    .put("data", infoList.size() > 10 ? infoList.subList(0, 10) : infoList));
        } catch (Exception e) {
            context.renderJSON(new JSONObject()
                    .put("result", -1)
                    .put("msg", "获取对象列表失败 " + e.getMessage()));
        }
    }

    public void showChat(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "chat.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        final JSONObject currentUser = Sessions.getUser();
        if (null == currentUser) {
            context.sendError(403);
            return;
        }
        // 放 ApiKey
        final String userId = currentUser.optString(Keys.OBJECT_ID);
        final String userPassword = currentUser.optString(User.USER_PASSWORD);
        final String userName = currentUser.optString(User.USER_NAME);
        final JSONObject cookieJSONObject = new JSONObject();
        cookieJSONObject.put(Keys.OBJECT_ID, userId);
        final String random = RandomStringUtils.randomAlphanumeric(16);
        cookieJSONObject.put(Keys.TOKEN, userPassword + ApiProcessor.COOKIE_ITEM_SEPARATOR + random);
        final String key = Crypts.encryptByAES(cookieJSONObject.toString(), Symphonys.COOKIE_SECRET);
        if (null != ApiProcessor.keys.get(userName)) {
            ApiProcessor.removeKeyByUsername(userName);
        }
        ApiProcessor.keys.put(key, currentUser);
        ApiProcessor.keys.put(userName, new JSONObject().put("key", key));
        dataModel.put("apiKey", key);

        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    public void hasUnreadChatMessage(RequestContext context) {
        context.renderJSON(new JSONObject().put("result", 0));
        JSONObject currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        String userId = currentUser.optString(Keys.OBJECT_ID);

        Query query = new Query().setFilter(new PropertyFilter("fromId", FilterOperator.EQUAL, userId));
        try {
            List<JSONObject> result = chatUnreadRepository.getList(query);
            if (result != null) {
                context.renderJSON(new JSONObject().put("result", result.size()));
            }
        } catch (RepositoryException ignored) {
        }
    }
}
