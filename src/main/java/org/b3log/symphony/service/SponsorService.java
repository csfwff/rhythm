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
import org.b3log.latke.Keys;
import org.b3log.latke.http.RequestContext;
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
import java.util.*;
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

    public double getSum(String userId) {
        try {
            List<JSONObject> data = repository.listByUserId(userId);
            double sum = 0;
            for (JSONObject i : data) {
                sum += i.optDouble("amount");
            }
            return sum;
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Cannot get user sum", e);
            return 0;
        }
    }

    public int getNo(final RequestContext context) {
        String userId = context.param("userId");
        int level = Integer.parseInt(context.param("level"));
        try {
            List<JSONObject> data = repository.listAsc();
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
