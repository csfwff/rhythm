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
import org.b3log.symphony.repository.ArticleRepository;
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
        String address = Requests.getRemoteAddr(context.getRequest());
        try {
            address = address.substring(0, (address.length() - 3));
            address = address + "*";
        } catch (Exception ignored) {
        }
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SS").format(new Date());
        log("simple", time, address, module, message, true);
    }
}
