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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.SponsorRepository;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author fangcong
 * @version 0.0.1
 * @since Created by work on 2022-02-12 22:58
 **/
@Service
public class SponsorService {

    private static final Logger LOGGER = LogManager.getLogger(SponsorService.class);

    @Inject
    private SponsorRepository repository;

    @Inject
    private UserQueryService userQueryService;

    public void add(final JSONObject record) {
        final Transaction transaction = repository.beginTransaction();
        try {
            repository.add(record);
            transaction.commit();
        } catch (RepositoryException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.warn("add sponsor record failed !");
        }
    }

    public List<JSONObject> list() {
        try {
            final List<JSONObject> list = repository.list();
            return list.stream().peek(x -> {
                x.put(User.USER_NAME, userQueryService.getUserName(x.optString(UserExt.USER_T_ID)));
                x.put(Common.DATE, new SimpleDateFormat("yyyy-MM-dd").format(x.optLong(Common.TIME)));
                x.put(Common.TIME, new SimpleDateFormat("HH:mm:ss").format(x.optLong(Common.TIME)));
                x.remove(UserExt.USER_T_ID);
            }).collect(Collectors.toList());
        } catch (RepositoryException e) {
            LOGGER.warn("add sponsor record list failed !");
            return Collections.emptyList();
        }
    }
}
