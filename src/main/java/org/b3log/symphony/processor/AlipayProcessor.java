package org.b3log.symphony.processor;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradeQueryResponse;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.Latkes;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.Response;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.Sponsor;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.processor.middleware.CSRFMidware;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.service.CloudService;
import org.b3log.symphony.service.NotificationMgmtService;
import org.b3log.symphony.service.PointtransferMgmtService;
import org.b3log.symphony.service.SponsorService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Singleton
public class AlipayProcessor {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(AlipayProcessor.class);

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);
        final CSRFMidware csrfMidware = beanManager.getReference(CSRFMidware.class);

        final AlipayProcessor alipayProcessor = beanManager.getReference(AlipayProcessor.class);
        Dispatcher.get("/pay/alipay", alipayProcessor::pay, loginCheck::handle);
    }

    final String CHARSET = "UTF-8";
    final static String APP_ID = Symphonys.get("pay.alipay.appId");
    final static String APP_PRIVATE_KEY = Symphonys.get("pay.alipay.appPrivateKey");
    final static String ALIPAY_PUBLIC_KEY = Symphonys.get("pay.alipay.alipayPublicKey");

    /**
     * Pay.
     *
     * @param context the specified context
     */
    public void pay(final RequestContext context) {
        Response response = context.getResponse();
        try {
            JSONObject currentUser = Sessions.getUser();
            try {
                currentUser = ApiProcessor.getUserByKey(context.param("apiKey"));
            } catch (NullPointerException ignored) {
            }
            final String userId = currentUser.optString(Keys.OBJECT_ID);

            if (context.param("total_amount") == null || context.param("subject_type") == null) {
                response.setContentType("text/html;charset="  + CHARSET);
                response.sendBytes("交易参数错误".getBytes(StandardCharsets.UTF_8));
                return;
            }
            final String OUT_TRADE_NO = "" + System.currentTimeMillis();
            String total_amount = context.param("total_amount");
            total_amount = String.format("%.2f", Double.parseDouble(total_amount));
            String subject_type = context.param("subject_type");
            String note = "没有备注 :)";
            if (context.param("note") != null) {
                note = context.param("note");
            }

            JSONObject biz = new JSONObject();
            biz.put("out_trade_no", OUT_TRADE_NO);
            biz.put("total_amount", total_amount);
            JSONObject trade = new JSONObject();
            trade.put("total_amount", total_amount);
            trade.put("note", note);
            trade.put("userId", userId);
            switch (subject_type) {
                case "001":
                    biz.put("subject", "捐助摸鱼派");
                    chargeTrades.put(OUT_TRADE_NO, trade);
                    break;
                default:
                    biz.put("subject", "捐助摸鱼派");
                    chargeTrades.put(OUT_TRADE_NO, trade);
                    break;
            }
            biz.put("product_code", "FAST_INSTANT_TRADE_PAY");

            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", CHARSET, ALIPAY_PUBLIC_KEY, "RSA2");
            AlipayTradePagePayRequest alipayRequest = new AlipayTradePagePayRequest();
            alipayRequest.setReturnUrl(Latkes.getStaticServePath() + "/charge/point");
            alipayRequest.setBizContent(biz.toString());
            String result = alipayClient.pageExecute(alipayRequest).getBody();

            response.setContentType("text/html;charset="  + CHARSET);
            response.sendBytes(result.getBytes(StandardCharsets.UTF_8));
        } catch (AlipayApiException e) {
            response.setContentType("text/html;charset="  + CHARSET);
            response.sendBytes("交易参数错误".getBytes(StandardCharsets.UTF_8));
            LOGGER.log(Level.ERROR, "Cannot trade", e);
        }
    }

    /**
     * Query trade.
     *
     * @param out_trade_no
     * @return
     */
    public static JSONObject query(String out_trade_no) {
        try {
            AlipayClient alipayClient = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", APP_ID, APP_PRIVATE_KEY, "json", "GBK", ALIPAY_PUBLIC_KEY, "RSA2");
            AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
            JSONObject biz = new JSONObject();
            biz.put("out_trade_no", out_trade_no);
            request.setBizContent(biz.toString());
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            return new JSONObject(response.getBody());
        } catch (AlipayApiException e) {
            LOGGER.log(Level.ERROR, "Query trade failed", e);
        }
        return new JSONObject().put("code", "-1").put("subMsg", "交易查询出错，请联系管理员检查支付接口配置");
    }

    // 捐助相关
    /**
     * String:
     * out_trade_no
     *
     * JSONObject:
     * total_amount: 交易金额
     * note: 备注
     * userId: 用户ID
     */
    public static Map<String, JSONObject> chargeTrades = new HashMap<>();

    /**
     * 检查是否有交易完成
     */
    public static synchronized void checkTrades() {
        try {
            Iterator<Map.Entry<String, JSONObject>> iterator = chargeTrades.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, JSONObject> entry = iterator.next();
                String out_trade_no = entry.getKey();
                // 查看是否过期
                long minute = (((System.currentTimeMillis() - Long.parseLong(out_trade_no)) / 1000) / 60);
                if (minute > 120) {
                    // 大于2个小时，直接删除
                    iterator.remove();
                    continue;
                }
                JSONObject result = query(out_trade_no);
                if (result.optJSONObject("alipay_trade_query_response").optString("code").equals("10000")) {
                    String total_amount = entry.getValue().optString("total_amount");
                    String note = entry.getValue().optString("note");
                    note = note.replaceAll("[^0-9a-zA-Z\\u4e00-\\u9fa5,，.。！!?？《》]", "");
                    String userId = entry.getValue().optString("userId");
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
                    final JSONObject notification = new JSONObject();
                    notification.put(Notification.NOTIFICATION_USER_ID, userId);
                    notification.put(Notification.NOTIFICATION_DATA_ID, transferId);
                    notificationMgmtService.addPointChargeNotification(notification);
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
                        cloudService.giveMetal(userId, L3_NAME, L3_DESC, L3_ATTR, "");
                    } else if (sum >= 256) {
                        cloudService.giveMetal(userId, L2_NAME, L2_DESC, L2_ATTR, "");
                    } else if (sum >= 16) {
                        cloudService.giveMetal(userId, L1_NAME, L1_DESC, L1_ATTR, "");
                    }
                    // 删除键
                    iterator.remove();
                }
            }
        } catch (Exception ignored) {
        }
    }

    // 勋章信息
    final static String L1_NAME = "摸鱼派粉丝";
    final static String L1_DESC = "捐助摸鱼派达16RMB";
    final static String L1_ATTR = "url=https://pwl.stackoverflow.wiki/2021/12/ht1-d8149de4.jpg&backcolor=ffffff&fontcolor=ff3030";

    final static String L2_NAME = "摸鱼派忠粉";
    final static String L2_DESC = "捐助摸鱼派达256RMB";
    final static String L2_ATTR = "url=https://pwl.stackoverflow.wiki/2021/12/ht2-bea67b29.jpg&backcolor=87cefa&fontcolor=efffff";

    final static String L3_NAME = "摸鱼派铁粉";
    final static String L3_DESC = "捐助摸鱼派达1024RMB";
    final static String L3_ATTR = "url=https://pwl.stackoverflow.wiki/2021/12/ht3-b97ea102.jpg&backcolor=ee3a8c&fontcolor=ffffff";
}
