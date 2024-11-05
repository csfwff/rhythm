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
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.SystemSettings;
import org.b3log.symphony.repository.SystemSettingsRepository;
import org.b3log.symphony.util.Sessions;
import org.json.JSONObject;

import java.util.Objects;

/**
 * @author fangcong
 * @version 0.0.1
 * @since Created by work on 2021-09-27 16:38
 **/
@Service
public class SystemSettingsService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(TagMgmtService.class);


    @Inject
    private SystemSettingsRepository settingsRepository;

    private static final String EMPTY_JSON = "{}";


    public JSONObject getByUsrId(final String userId) {
        try {
            return settingsRepository.getByUsrId(userId);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, String.format("user: %s not found in db", userId), e);
            return null;
        }

    }


    /**
     * set system title the specified settings.
     *
     * @param jsonObject the specified settings
     */
    public synchronized void setSystemSettings(final JSONObject jsonObject) throws RepositoryException {
        final JSONObject currentUser = Sessions.getUser();
        if (Objects.isNull(currentUser)) {
            return;
        }
        final String userId = currentUser.optString(Keys.OBJECT_ID);

        try {
            final JSONObject settings = settingsRepository.getByUsrId(userId);
            if (Objects.isNull(settings)) {
                //init
                initSettings(userId, jsonObject);
            } else {
                updateSettings(settings, jsonObject);
            }
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, String.format("user: %s set system title failed", userId), e);
            throw e;
        }
    }

    public void updateSettings(final JSONObject settings, final JSONObject updatedSettings) {
        final String id = settings.optString(Keys.OBJECT_ID);
        final Transaction transaction = settingsRepository.beginTransaction();
        try {
            settings.put(SystemSettings.SETTINGS, updatedSettings.toString());
            settingsRepository.update(id, settings);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            LOGGER.log(Level.ERROR, "Updates a settings [id=" + id + "] failed", e);
        }
        transaction.commit();
    }


    public void initSettings(final String userId, final JSONObject settings) {
        final Transaction transaction = settingsRepository.beginTransaction();
        try {
            final JSONObject initSettings = new JSONObject();
            initSettings.put("userId", userId);
            initSettings.put(SystemSettings.SETTINGS, EMPTY_JSON);
            if (Objects.nonNull(settings) && !settings.isEmpty()) {
                initSettings.put(SystemSettings.SETTINGS, settings.toString());
            }
            settingsRepository.add(initSettings);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Cannot init settings to the database.", e);
        }
        transaction.commit();
    }
}
