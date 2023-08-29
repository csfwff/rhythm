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
package org.b3log.symphony.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Requests;
import org.b3log.symphony.processor.LogsProcessor;
import org.b3log.symphony.processor.channel.LogsChannel;
import org.b3log.symphony.repository.ChatInfoRepository;
import org.b3log.symphony.repository.ChatRoomRepository;
import org.b3log.symphony.repository.LogsRepository;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author fangcong
 * @version 0.0.1
 * @since Created by work on 2022-02-12 22:58
 **/
@Service
public class LogsService {

    private static final Logger LOGGER = LogManager.getLogger(LogsService.class);

    public static void log(String type, String key1, String key2, String key3, String data, boolean isPublic) {
        try {
            // 写表
            final BeanManager beanManager = BeanManager.getInstance();
            final LogsRepository logsRepository = beanManager.getReference(LogsRepository.class);
            final Transaction transaction = logsRepository.beginTransaction();
            logsRepository.add(type, key1, key2, key3, data, isPublic);
            transaction.commit();

            // 向WS发送消息
            if (isPublic) {
                JSONObject messageJSON = new JSONObject();
                messageJSON.put("type", type);
                messageJSON.put("key1", key1);
                messageJSON.put("key2", key2);
                messageJSON.put("key3", key3);
                messageJSON.put("data", data);
                LogsChannel.sendMsg(messageJSON.toString());
            }
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Unable to log", e);
        }
    }

    public static void simpleLog(RequestContext context, String module, String message) {
        log("simple", getTime(), getAddress(context), module, message, true);
    }

    public static void chatroomLog(RequestContext context, String oId, String userName) {
        final BeanManager beanManager = BeanManager.getInstance();
        final ChatRoomRepository chatRoomRepository = beanManager.getReference(ChatRoomRepository.class);
        try {
            JSONObject messageInfo = chatRoomRepository.get(oId);
            log("cr-revoke", getTime(), getAddress(context), userName, messageInfo.toString(), false);
        } catch (Exception ignored) {
        }
    }

    public static void commentLog(RequestContext context, String userName, JSONObject text) {
        try {
            log("comment-remove", getTime(), getAddress(context), userName, text.toString(), false);
        } catch (Exception ignored) {
        }
    }

    public static void chatLog(RequestContext context, String oId, String userName) {
        final BeanManager beanManager = BeanManager.getInstance();
        final ChatInfoRepository chatInfoRepository = beanManager.getReference(ChatInfoRepository.class);
        try {
            JSONObject messageInfo = chatInfoRepository.get(oId);
            log("chat-revoke", getTime(), getAddress(context), userName, messageInfo.toString(), false);
        } catch (Exception ignored) {
        }
    }

    public static String getTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public static String getAddress(RequestContext context) {
        String address = Requests.getRemoteAddr(context.getRequest());
        try {
            String[] splitAddress = address.split("\\.");
            address = "";
            for (int i = 0; i < splitAddress.length - 1; i++) {
                address += splitAddress[i] + ".";
            }
            address += "*";
        } catch (Exception ignored) {
        }
        return address;
    }
}
