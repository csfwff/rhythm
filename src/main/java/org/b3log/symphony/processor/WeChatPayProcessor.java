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

import com.alipay.api.internal.util.file.Charsets;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.Request;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.ServiceException;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.Sponsor;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.SponsorRepository;
import org.b3log.symphony.service.CloudService;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.service.SponsorService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.apache.logging.log4j.core.util.NameUtil.md5;

@Singleton
public class WeChatPayProcessor {

    private static final Logger LOGGER = LogManager.getLogger(WeChatPayProcessor.class);

    Map<String, String> tradeMap = new HashMap<>();

    // 勋章信息
    final static String L1_NAME = "摸鱼派粉丝";
    final static String L1_DESC = "捐助摸鱼派达16RMB; 编号No.";
    final static String L1_ATTR = "url=https://file.fishpi.cn/2021/12/ht1-d8149de4.jpg&backcolor=ffffff&fontcolor=ff3030";
    final static String L2_NAME = "摸鱼派忠粉";
    final static String L2_DESC = "捐助摸鱼派达256RMB; 编号No.";
    final static String L2_ATTR = "url=https://file.fishpi.cn/2021/12/ht2-bea67b29.jpg&backcolor=87cefa&fontcolor=efffff";
    final static String L3_NAME = "摸鱼派铁粉";
    final static String L3_DESC = "捐助摸鱼派达1024RMB; 编号No.";
    final static String L3_ATTR = "url=https://file.fishpi.cn/2021/12/ht3-b97ea102.jpg&backcolor=ee3a8c&fontcolor=ffffff";

    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final WeChatPayProcessor weChatPayProcessor = beanManager.getReference(WeChatPayProcessor.class);
        Dispatcher.get("/pay/wechat", weChatPayProcessor::pay, loginCheck::handle);
        Dispatcher.post("/pay/wechatCall", weChatPayProcessor::wechatCall);
    }

    synchronized public void wechatCall(final RequestContext context) {
        final Request request = context.getRequest();
        Set<String> params = request.getParameterNames();
        Map<String, String> paramsMap = new HashMap<>();
        for (String param : params) {
            paramsMap.put(param, request.getParameter(param));
            System.out.println("key: " + param + "\nvalue: " + request.getParameter(param));
        }
        String out_trade_no = paramsMap.get("out_trade_no");
        if (!tradeMap.containsKey(out_trade_no)) {
            final Response response = context.getResponse();
            response.sendString("FAIL");
            return;
        }
        String note = tradeMap.get(out_trade_no);
        if (note == null) {
            note = "没有备注 :)";
        }
        tradeMap.remove(out_trade_no);

        // 校验签名
        String sign = paramsMap.get("sign");
        Map <String, String> params2 = new HashMap<>();
        params2.put("code", paramsMap.get("code"));
        params2.put("timestamp", paramsMap.get("timestamp"));
        params2.put("mch_id", paramsMap.get("mch_id"));
        params2.put("order_no", paramsMap.get("order_no"));
        params2.put("out_trade_no", paramsMap.get("out_trade_no"));
        params2.put("pay_no", paramsMap.get("pay_no"));
        params2.put("total_fee", paramsMap.get("total_fee"));
        String key = Symphonys.get("pay.wechat.key");
        String localSign = createSign(params2, key);
        if (!localSign.equals(sign)) {
            final Response response = context.getResponse();
            response.sendString("FAIL");
            return;
        }

        String userId = paramsMap.get("attach");
        String total_amount = paramsMap.get("total_fee");
        // 添加捐助信息
        final BeanManager beanManager = BeanManager.getInstance();
        PointtransferMgmtService pointtransferMgmtService = beanManager.getReference(PointtransferMgmtService.class);
        NotificationMgmtService notificationMgmtService = beanManager.getReference(NotificationMgmtService.class);
        SponsorService sponsorService = beanManager.getReference(SponsorService.class);
        CloudService cloudService = beanManager.getReference(CloudService.class);
        int point;
        double total = Double.parseDouble(total_amount);
        point = ((int) total) * 80;
        if (point == 0) {
            point = 1;
        }
        // 打积分
        final String transferId = pointtransferMgmtService.transfer(Pointtransfer.ID_C_SYS, userId,
                Pointtransfer.TRANSFER_TYPE_C_CHARGE, point, total_amount, System.currentTimeMillis(), "");
        // 通知
        try {
            final JSONObject notification = new JSONObject();
            notification.put(Notification.NOTIFICATION_USER_ID, userId);
            notification.put(Notification.NOTIFICATION_DATA_ID, transferId);
            notificationMgmtService.addPointChargeNotification(notification);
        } catch (ServiceException e) {
            LOGGER.error(e.getMessage());
        }
        // 保存捐赠记录
        final JSONObject record = new JSONObject();
        record.put(UserExt.USER_T_ID, userId);
        record.put("time", System.currentTimeMillis());
        record.put(Sponsor.SPONSOR_MESSAGE, note);
        record.put(Sponsor.AMOUNT, total);
        sponsorService.add(record);
        // 统计用户总积分
        double sum = sponsorService.getSum(userId);
        // 三清
        cloudService.removeMetal(userId, L1_NAME);
        cloudService.removeMetal(userId, L2_NAME);
        cloudService.removeMetal(userId, L3_NAME);
        // 赋予勋章
        if (sum >= 1024) {
            cloudService.giveMetal(userId, L3_NAME, L3_DESC + getNo(userId, 3), L3_ATTR, "");
            cloudService.giveMetal(userId, L2_NAME, L2_DESC + getNo(userId, 2), L2_ATTR, "");
            cloudService.giveMetal(userId, L1_NAME, L1_DESC + getNo(userId, 1), L1_ATTR, "");
        } else if (sum >= 256) {
            cloudService.giveMetal(userId, L2_NAME, L2_DESC + getNo(userId, 2), L2_ATTR, "");
            cloudService.giveMetal(userId, L1_NAME, L1_DESC + getNo(userId, 1), L1_ATTR, "");
        } else if (sum >= 16) {
            cloudService.giveMetal(userId, L1_NAME, L1_DESC + getNo(userId, 1), L1_ATTR, "");
        }

        final Response response = context.getResponse();
        response.sendString("SUCCESS");
    }

    public void pay(final RequestContext context) {
        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
        } catch (NullPointerException ignored) {
        }
        final String userId = currentUser.optString(Keys.OBJECT_ID);

        if (context.param("total_amount") == null) {
            context.renderJSON(StatusCodes.ERR);
            return;
        }
        long time = System.currentTimeMillis() / 1000;
        String total_amount = context.param("total_amount");
        total_amount = String.format("%.2f", Double.parseDouble(total_amount));
        String note = "没有备注 :)";
        if (context.param("note") != null) {
            note = context.param("note");
        }
        note = note.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5,，.。！!?？《》]", "");
        if (note.length() > 32) {
            note = note.substring(0, 32);
        }

        String mchId = Symphonys.get("pay.wechat.mch_id");
        String key = Symphonys.get("pay.wechat.key");

        Map <String, String> params = new HashMap<>();
        params.put("mch_id", mchId);
        params.put("out_trade_no", String.valueOf(time));
        params.put("total_fee", total_amount);
        params.put("body", "捐助摸鱼派");
        params.put("timestamp", String.valueOf(time));
        params.put("notify_url", "https://fishpi.cn/pay/wechatCall");
        String sign = createSign(params, key);
        String param = getParam(params, key);

        tradeMap.put(String.valueOf(time), note);

        final HttpResponse response = HttpRequest.post("https://api.ltzf.cn/api/wxpay/native")
                .header("content-type", "application/x-www-form-urlencoded")
                .bodyText(param + "&attach=" + userId + "&time_expire=2h&sign=" + sign)
                .connectionTimeout(5000).timeout(5000).send();
        response.charset("UTF-8");
        final JSONObject ret = new JSONObject(response.bodyText());
        if (200 != response.statusCode()) {
            context.renderJSON(StatusCodes.ERR);
            LOGGER.warn(ret.toString(4));
            return;
        }
        context.renderJSON(new JSONObject().put("QRcode_url", ret.optJSONObject("data").optString("QRcode_url")));
    }

    public static String packageSign(Map < String, String > params, boolean urlEncoder) {
        // 先将参数以其参数名的字典序升序进行排序
        TreeMap < String, String > sortedParams = new TreeMap< String, String >(params);
        // 遍历排序后的字典，将所有参数按"key=value"格式拼接在一起
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry< String, String > param: sortedParams.entrySet()) {
            String value = param.getValue();
            if (value.isEmpty()) {
                continue;
            }
            if (first) {
                first = false;
            } else {
                sb.append("&");
            }
            sb.append(param.getKey()).append("=");
            if (urlEncoder) {
                try {
                    value = urlEncode(value);
                } catch (UnsupportedEncodingException e) {}
            }
            sb.append(value);
        }
        return sb.toString();
    }

    public static String urlEncode(String src) throws UnsupportedEncodingException {
        return URLEncoder.encode(src, Charsets.UTF_8.name()).replace("+", "%20");
    }

    public static String createSign(Map < String, String > params, String partnerKey) {
        // 生成签名前先去除sign
        params.remove("sign");
        String stringA = packageSign(params, false);
        String stringSignTemp = stringA + "&key=" + partnerKey;
        return md5(stringSignTemp).toUpperCase();
    }

    public static String getParam(Map < String, String > params, String partnerKey) {
        String stringA = packageSign(params, false);
        return stringA;
    }

    public static int getNo(String userId, int level) {
        try {
            final BeanManager beanManager = BeanManager.getInstance();
            final SponsorRepository sponsorRepository = beanManager.getReference(SponsorRepository.class);
            List<JSONObject> data = sponsorRepository.listAsc();
            HashMap<String, Double> map = new HashMap<>();
            List<String> rank = new ArrayList<>();
            List<String> ignores = new ArrayList<>();
            for (JSONObject i : data) {
                String id = i.optString("userId");
                double amount = i.optDouble("amount");
                if (!ignores.contains(id)) {
                    if (map.containsKey(id)) {
                        switch (level) {
                            case 1:
                                if (map.get(id) + amount >= 16) {
                                    rank.add(id);
                                    ignores.add(id);
                                } else {
                                    map.put(id, map.get(id) + amount);
                                }
                                break;
                            case 2:
                                if (map.get(id) + amount >= 256) {
                                    rank.add(id);
                                    ignores.add(id);
                                } else {
                                    map.put(id, map.get(id) + amount);
                                }
                                break;
                            case 3:
                                if (map.get(id) + amount >= 1024) {
                                    rank.add(id);
                                    ignores.add(id);
                                } else {
                                    map.put(id, map.get(id) + amount);
                                }
                                break;
                        }
                    } else {
                        switch (level) {
                            case 1:
                                if (amount >= 16) {
                                    rank.add(id);
                                    ignores.add(id);
                                } else {
                                    map.put(id, amount);
                                }
                                break;
                            case 2:
                                if (amount >= 256) {
                                    rank.add(id);
                                    ignores.add(id);
                                } else {
                                    map.put(id, amount);
                                }
                                break;
                            case 3:
                                if (amount >= 1024) {
                                    rank.add(id);
                                    ignores.add(id);
                                } else {
                                    map.put(id, amount);
                                }
                                break;
                        }
                    }
                }
            }
            for (int i = 0; i < rank.size(); i++) {
                String id = rank.get(i);
                if (id.equals(userId)) {
                    return i + 1;
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Cannot get user No", e);
        }

        return -1;
    }
}
