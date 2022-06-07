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

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.processor.channel.ShopChannel;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.ShopRepository;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

import java.util.Map;

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

    public void runCmd(RequestContext context) {
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
        switch (command.split(" ")[0]) {
            case "help":
                ShopChannel.sendMsg(userId, "<br>" +
                        "===== 系统商店帮助菜单 =====<br>" +
                        "help 打印帮助菜单<br>" +
                        "history 查看输入的历史命令 (支持通过上下按键快捷切换)<br>" +
                        "list buy 查看可供购买的商品列表<br>" +
                        "list sell 查看商店收购中的商品列表");
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

                        break;
                    case "sell":

                        break;
                    default:
                        ShopChannel.sendMsg(userId, "" +
                                "命令有误，输入\"help\"获取帮助信息。");
                        break;
                }
                break;
            default:
                ShopChannel.sendMsg(userId, "" +
                        "命令有误，输入\"help\"获取帮助信息。");
        }
    }
}
