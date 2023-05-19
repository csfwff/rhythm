/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.processor;

import org.apache.commons.lang.time.DateFormatUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.Transaction;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.channel.ShopChannel;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.ShopRepository;
import org.b3log.symphony.service.CloudService;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Singleton
public class ShopProcessor {

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * Shop repository.
     */
    @Inject
    private ShopRepository shopRepository;

    /**
     * Cloud service.
     */
    @Inject
    private CloudService cloudService;

    @Inject
    private NotificationMgmtService notificationMgmtService;

    /**
     * Pointtransfer management service.
     */
    @Inject
    private PointtransferMgmtService pointtransferMgmtService;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final ShopProcessor shopProcessor = beanManager.getReference(ShopProcessor.class);
        Dispatcher.get("/shop", shopProcessor::showShop, loginCheck::handle);
        Dispatcher.post("/shop", shopProcessor::runCmd, loginCheck::handle);
    }

    public void showShop(RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "shop/index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    public synchronized void runCmd(RequestContext context) {
        context.renderJSON(StatusCodes.SUCC);
        context.renderMsg("OK");
        final JSONObject requestJSONObject = context.requestJSON();
        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
        } catch (NullPointerException ignored) {
        }
        String userId = currentUser.optString(Keys.OBJECT_ID);
        String command = requestJSONObject.optString("command");
        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
        int id = -1;
        int sum = -1;
        switch (command.split(" ")[0]) {
            case "help":
                ShopChannel.sendMsg(userId, "<br>" +
                        "===== 系统商店帮助菜单 =====<br>" +
                        "help 打印帮助菜单<br>" +
                        "history 查看输入的历史命令 (支持通过上下按键快捷切换)<br>" +
                        "list buy 查看可供购买的商品列表<br>" +
                        "list sell 查看商店收购中的商品列表<br>" +
                        "sell [商品ID] [出售数量] 出售商品<br>" +
                        "buy [商品ID] [购买数量] 购买商品");
                break;
            case "list":
                String type = "";
                try {
                    type = command.split(" ")[1];
                } catch (Exception e) {
                    ShopChannel.sendMsg(userId, "" +
                            "命令有误，输入\"help\"获取帮助信息。");
                    break;
                }
                switch (type) {
                    case "buy":
                        try {
                            List<JSONObject> buyList = shopRepository.getByType("buy");
                            if (buyList.isEmpty()) {
                                ShopChannel.sendMsg(userId, "" +
                                        "暂无可供购买的商品，敬请期待！");
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("<br>");
                            for (int i = 0; i < buyList.size(); i++) {
                                JSONObject sell = buyList.get(i);
                                JSONObject buyObject = new JSONObject(sell.optString("data"));
                                JSONObject limitObject = new JSONObject(buyObject.optString("limit"));
                                stringBuilder.append("⭐️=====---------=====⭐️<br>");
                                stringBuilder.append("商品ID：" + i + "<br>");
                                stringBuilder.append("出售：" + buyObject.optString("name") + "<br>");
                                stringBuilder.append("描述：" + buyObject.optString("description") + "<br>");
                                stringBuilder.append("价格：" + buyObject.optString("price") + "<br>");
                                stringBuilder.append("每人每日限购：" + limitObject.optString("per") + "<br>");
                                stringBuilder.append("每日总限购：" + limitObject.optString("maxPerDay") + "<br>");
                                stringBuilder.append("购买本商品请使用命令 <font color='red'>buy " + i + " [购买数量]</font><br>");
                                stringBuilder.append("⭐️=====---------=====⭐️");
                                if (i != buyList.size() - 1) {
                                    stringBuilder.append("<br>");
                                }
                            }
                            ShopChannel.sendMsg(userId, stringBuilder.toString());
                        } catch (Exception e) {
                            ShopChannel.sendMsg(userId, "" +
                                    "获取列表有误，请联系管理员。");
                        }
                        break;
                    case "sell":
                        try {
                            List<JSONObject> sellList = shopRepository.getByType("sell");
                            if (sellList.isEmpty()) {
                                ShopChannel.sendMsg(userId, "" +
                                        "暂无收购中的商品，敬请期待！");
                            }
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("<br>");
                            for (int i = 0; i < sellList.size(); i++) {
                                JSONObject sell = sellList.get(i);
                                JSONObject sellObject = new JSONObject(sell.optString("data"));
                                JSONObject limitObject = new JSONObject(sellObject.optString("limit"));
                                stringBuilder.append("⭐️=====---------=====⭐️<br>");
                                stringBuilder.append("商品ID：" + i + "<br>");
                                stringBuilder.append("收购：" + sellObject.optString("name") + "<br>");
                                stringBuilder.append("描述：" + sellObject.optString("description") + "<br>");
                                stringBuilder.append("价格：" + sellObject.optString("price") + "<br>");
                                stringBuilder.append("每人每日限售：" + limitObject.optString("per") + "<br>");
                                stringBuilder.append("每日总限售：" + limitObject.optString("maxPerDay") + "<br>");
                                stringBuilder.append("出售本商品请使用命令 <font color='red'>sell " + i + " [出售数量]</font><br>");
                                stringBuilder.append("⭐️=====---------=====⭐️");
                                if (i != sellList.size() - 1) {
                                    stringBuilder.append("<br>");
                                }
                            }
                            ShopChannel.sendMsg(userId, stringBuilder.toString());
                        } catch (Exception e) {
                            ShopChannel.sendMsg(userId, "" +
                                    "获取列表有误，请联系管理员。");
                        }
                        break;
                    default:
                        ShopChannel.sendMsg(userId, "" +
                                "命令有误，输入\"help\"获取帮助信息。");
                        break;
                }
                break;
            case "sell":
                try {
                    id = Integer.parseInt(command.split(" ")[1]);
                    sum = Integer.parseInt(command.split(" ")[2]);
                    if (id >= 0 && sum > 0) {
                        List<JSONObject> sellList = shopRepository.getByType("sell");
                        try {
                            JSONObject sell = sellList.get(id);
                            JSONObject sellObject = new JSONObject(sell.optString("data"));
                            JSONObject limitObject = new JSONObject(sellObject.optString("limit"));
                            JSONArray tradersObject = new JSONArray(sellObject.optString("traders"));
                            // 检查每日总限购
                            int maxPerDay = limitObject.optInt("maxPerDay");
                            int tradersToday = 0;
                            for (int i = 0; i < tradersObject.length(); i++) {
                                JSONObject traderObject = tradersObject.optJSONObject(i);
                                if (traderObject.optString("latestTradeDate").equals(date)) {
                                    int trades = traderObject.optInt("trades");
                                    tradersToday += trades;
                                }
                            }
                            if (tradersToday >= maxPerDay) {
                                ShopChannel.sendMsg(userId, "" +
                                        "今日总限售已到封顶，无法交易，明天再来吧！");
                                return;
                            }
                            // 检查单人限售
                            int maxPer = limitObject.optInt("per");
                            for (int i = 0; i < tradersObject.length(); i++) {
                                JSONObject traderObject = tradersObject.optJSONObject(i);
                                String traderUserId = traderObject.optString("userId");
                                if (traderUserId.equals(userId)) {
                                    String latestTradeDate = traderObject.optString("latestTradeDate");
                                    if (latestTradeDate.equals(date)) {
                                        int trades = traderObject.optInt("trades");
                                        if (trades >= maxPer) {
                                            ShopChannel.sendMsg(userId, "" +
                                                    "你今天已经卖得够多啦，无法继续交易，明天再来吧！");
                                            return;
                                        }
                                        if (sum > (maxPer - trades)) {
                                            ShopChannel.sendMsg(userId, "" +
                                                    "你今天最多只能再出售 " + (maxPer - trades) + " 个当前商品。");
                                            return;
                                        }
                                    }
                                }
                            }
                            if (sum > maxPer) {
                                ShopChannel.sendMsg(userId, "" +
                                        "你的交易数量大于单人限制交易数量，交易终止！");
                                return;
                            }
                            // 检查是否有足够的物品可供交易
                            String realName = sellObject.optString("realName");
                            String name = sellObject.optString("name");
                            JSONObject bagJSON = new JSONObject(cloudService.getBag(userId));
                            int has = bagJSON.optInt(realName);
                            if (has < sum) {
                                ShopChannel.sendMsg(userId, "" +
                                        "你的背包中没有足够的物品可供收购，背包中" + name + "数量：" + has);
                                return;
                            }
                            // OK了，可以交易
                            if (cloudService.putBag(userId, realName, (sum - (sum * 2)), Integer.MAX_VALUE) == 0) {
                                int price = sellObject.optInt("price");
                                int total = sum * price;
                                // 加积分
                                final String transferId = pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, userId,
                                        Pointtransfer.TRANSFER_TYPE_C_TRADE, total, userId, System.currentTimeMillis(), "系统商店收购：" + sum + "件 " + name);
                                final JSONObject notification = new JSONObject();
                                notification.put(Notification.NOTIFICATION_USER_ID, userId);
                                notification.put(Notification.NOTIFICATION_DATA_ID, transferId);
                                notificationMgmtService.addPointTransferNotification(notification);
                                // 写JSON
                                boolean changed = false;
                                for (int i = 0; i < tradersObject.length(); i++) {
                                    JSONObject traderObject = tradersObject.optJSONObject(i);
                                    String traderUserId = traderObject.optString("userId");
                                    if (traderUserId.equals(userId)) {
                                        changed = true;
                                        String latestTradeDate = traderObject.optString("latestTradeDate");
                                        if (!latestTradeDate.equals(date)) {
                                            traderObject.put("latestTradeDate", date);
                                            traderObject.put("trades", 0);
                                        }
                                        int trades = traderObject.optInt("trades");
                                        traderObject.put("trades", trades + sum);
                                        tradersObject.remove(i);
                                        tradersObject.put(traderObject);
                                        break;
                                    }
                                }
                                if (!changed || tradersObject.isEmpty()) {
                                    JSONObject traderObject = new JSONObject();
                                    traderObject.put("userId", userId);
                                    traderObject.put("latestTradeDate", date);
                                    traderObject.put("trades", sum);
                                    tradersObject.put(traderObject);
                                }
                                sellObject.put("traders", tradersObject);
                                sell.put("data", sellObject.toString());

                                final Transaction transaction = shopRepository.beginTransaction();
                                shopRepository.update(sell.optString(Keys.OBJECT_ID), sell);
                                transaction.commit();

                                ShopChannel.sendMsg(userId, "" +
                                        "交易成功！积分 " + total + " 已打入你的账户中。");
                                return;
                            }
                        } catch (Exception e) {
                            ShopChannel.sendMsg(userId, "" +
                                    "出售的商品ID不存在或出现错误。<br><font color='brown'>" + e.getMessage() + "</font><br>");
                            return;
                        }
                    } else {
                        ShopChannel.sendMsg(userId, "" +
                                "交易命令不合法，请检查。");
                        return;
                    }
                } catch (Exception e) {
                    ShopChannel.sendMsg(userId, "" +
                            "命令有误，输入\"help\"获取帮助信息。");
                    return;
                }
                break;
            case "buy":
                try {
                    id = Integer.parseInt(command.split(" ")[1]);
                    sum = Integer.parseInt(command.split(" ")[2]);
                    if (id >= 0 && sum > 0) {
                        List<JSONObject> buyList = shopRepository.getByType("buy");
                        try {
                            JSONObject buy = buyList.get(id);
                            JSONObject buyObject = new JSONObject(buy.optString("data"));
                            JSONObject limitObject = new JSONObject(buyObject.optString("limit"));
                            JSONArray tradersObject = new JSONArray(buyObject.optString("traders"));
                            // 检查每日总限购
                            int maxPerDay = limitObject.optInt("maxPerDay");
                            int tradersToday = 0;
                            for (int i = 0; i < tradersObject.length(); i++) {
                                JSONObject traderObject = tradersObject.optJSONObject(i);
                                if (traderObject.optString("latestTradeDate").equals(date)) {
                                    int trades = traderObject.optInt("trades");
                                    tradersToday += trades;
                                }
                            }
                            if (tradersToday >= maxPerDay) {
                                ShopChannel.sendMsg(userId, "" +
                                        "今日总限购已到封顶，无法交易，明天再来吧！");
                                return;
                            }
                            // 检查单人限购
                            int maxPer = limitObject.optInt("per");
                            for (int i = 0; i < tradersObject.length(); i++) {
                                JSONObject traderObject = tradersObject.optJSONObject(i);
                                String traderUserId = traderObject.optString("userId");
                                if (traderUserId.equals(userId)) {
                                    String latestTradeDate = traderObject.optString("latestTradeDate");
                                    if (latestTradeDate.equals(date)) {
                                        int trades = traderObject.optInt("trades");
                                        if (trades >= maxPer) {
                                            ShopChannel.sendMsg(userId, "" +
                                                    "你的口袋已经装满啦，无法继续交易，明天再来吧！");
                                            return;
                                        }
                                        if (sum > (maxPer - trades)) {
                                            ShopChannel.sendMsg(userId, "" +
                                                    "你今天最多只能再购买 " + (maxPer - trades) + " 个当前商品。");
                                            return;
                                        }
                                    }
                                }
                            }
                            if (sum > maxPer) {
                                ShopChannel.sendMsg(userId, "" +
                                        "你的交易数量大于单人限制交易数量，交易终止！");
                                return;
                            }
                            // 检查是否有足够的积分可供交易
                            String realName = buyObject.optString("realName");
                            String name = buyObject.optString("name");
                            int userPoint = currentUser.optInt(UserExt.USER_POINT);
                            int price = buyObject.optInt("price");
                            int total = sum * price;
                            if (userPoint < total) {
                                ShopChannel.sendMsg(userId, "" +
                                        "你的积分不足，积分余额：" + userPoint + "，购买需要：" + total);
                                return;
                            }
                            // OK了，可以交易
                            if (cloudService.putBag(userId, realName, sum, Integer.MAX_VALUE) == 0) {
                                // 扣积分
                                final String transferId = pointtransferMgmtService.transfer(userId, Pointtransfer.ID_C_SYS,
                                        Pointtransfer.TRANSFER_TYPE_C_TRADE, total, "系统商店购买：" + sum + "件 " + name, System.currentTimeMillis(), "系统商店购买：" + sum + "件 " + name);
                                final JSONObject notification = new JSONObject();
                                notification.put(Notification.NOTIFICATION_USER_ID, userId);
                                notification.put(Notification.NOTIFICATION_DATA_ID, transferId);
                                notificationMgmtService.addAbusePointDeductNotification(notification);
                                // 写JSON
                                boolean changed = false;
                                for (int i = 0; i < tradersObject.length(); i++) {
                                    JSONObject traderObject = tradersObject.optJSONObject(i);
                                    String traderUserId = traderObject.optString("userId");
                                    if (traderUserId.equals(userId)) {
                                        changed = true;
                                        String latestTradeDate = traderObject.optString("latestTradeDate");
                                        if (!latestTradeDate.equals(date)) {
                                            traderObject.put("latestTradeDate", date);
                                            traderObject.put("trades", 0);
                                        }
                                        int trades = traderObject.optInt("trades");
                                        traderObject.put("trades", trades + sum);
                                        tradersObject.remove(i);
                                        tradersObject.put(traderObject);
                                        break;
                                    }
                                }
                                if (!changed || tradersObject.isEmpty()) {
                                    JSONObject traderObject = new JSONObject();
                                    traderObject.put("userId", userId);
                                    traderObject.put("latestTradeDate", date);
                                    traderObject.put("trades", sum);
                                    tradersObject.put(traderObject);
                                }
                                buyObject.put("traders", tradersObject);
                                buy.put("data", buyObject.toString());

                                final Transaction transaction = shopRepository.beginTransaction();
                                shopRepository.update(buy.optString(Keys.OBJECT_ID), buy);
                                transaction.commit();

                                ShopChannel.sendMsg(userId, "" +
                                        "交易成功！积分 " + total + " 已你的账户中扣除。");
                                return;
                            }
                        } catch (Exception e) {
                            ShopChannel.sendMsg(userId, "" +
                                    "购买的商品ID不存在或出现错误。<br><font color='brown'>" + e.getMessage() + "</font><br>");
                            return;
                        }
                    } else {
                        ShopChannel.sendMsg(userId, "" +
                                "交易命令不合法，请检查。");
                        return;
                    }
                } catch (Exception e) {
                    ShopChannel.sendMsg(userId, "" +
                            "命令有误，输入\"help\"获取帮助信息。");
                    return;
                }
                break;
            default:
                ShopChannel.sendMsg(userId, "" +
                        "命令有误，输入\"help\"获取帮助信息。");
        }
    }
}
