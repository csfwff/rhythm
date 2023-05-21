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
package org.b3log.symphony.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.symphony.service.ChatListService;
import org.json.JSONObject;


/**
 * 私聊发送事件
 *
 * @author fangcong
 * @version 0.0.1
 * @since Created by work on 2022-02-22 13:28
 **/
@Singleton
public class PrivateChatSendHandler extends AbstractEventListener<JSONObject> {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(PrivateChatSendHandler.class);

    @Inject
    private ChatListService chatListService;

    @Override
    public String getEventType() {
        return EventTypes.PRIVATE_CHAT;
    }

    @Override
    public void action(Event<JSONObject> event) {
        final JSONObject message = event.getData();
        try {
            chatListService.update(message);
        } catch (Exception e) {
            LOGGER.error("process private chat send event error", e);
        }


    }
}
