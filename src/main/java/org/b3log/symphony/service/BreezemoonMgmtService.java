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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Breezemoon;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.repository.BreezemoonRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.ReservedWords;
import org.json.JSONObject;

/**
 * Breezemoon management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.3, Nov 22, 2018
 * @since 2.8.0
 */
@Service
public class BreezemoonMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(BreezemoonMgmtService.class);

    /**
     * Breezemoon repository.
     */
    @Inject
    private BreezemoonRepository breezemoonRepository;

    /**
     * User repository.
     */
    @Inject
    private UserRepository userRepository;

    /**
     * Option query service.
     */
    @Inject
    private OptionQueryService optionQueryService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Adds a breezemoon with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "breezemoonContent": "",
     *                          "breezemoonAuthorId": "",
     *                          "breezemoonIP": "",
     *                          "breezemoonUA": "",
     *                          "breezemoonCity": ""
     * @throws ServiceException service exception
     */
    @Transactional
    public String addBreezemoon(final JSONObject requestJSONObject) throws ServiceException {
        requestJSONObject.put(Breezemoon.BREEZEMOON_CONTENT, ReservedWords.processReservedWord(requestJSONObject.optString(Breezemoon.BREEZEMOON_CONTENT)));
        final String content = requestJSONObject.optString(Breezemoon.BREEZEMOON_CONTENT);
        final JSONObject bm = new JSONObject();
        bm.put(Breezemoon.BREEZEMOON_CONTENT, content);
        bm.put(Breezemoon.BREEZEMOON_AUTHOR_ID, requestJSONObject.optString(Breezemoon.BREEZEMOON_AUTHOR_ID));
        bm.put(Breezemoon.BREEZEMOON_IP, requestJSONObject.optString(Breezemoon.BREEZEMOON_IP));
        String ua = requestJSONObject.optString(Breezemoon.BREEZEMOON_UA);
        if (StringUtils.length(ua) > Common.MAX_LENGTH_UA) {
            ua = StringUtils.substring(ua, 0, Common.MAX_LENGTH_UA);
        }
        bm.put(Breezemoon.BREEZEMOON_UA, ua);
        final long now = System.currentTimeMillis();
        bm.put(Breezemoon.BREEZEMOON_CREATED, now);
        bm.put(Breezemoon.BREEZEMOON_UPDATED, now);
        bm.put(Breezemoon.BREEZEMOON_STATUS, Breezemoon.BREEZEMOON_STATUS_C_VALID);
        bm.put(Breezemoon.BREEZEMOON_CITY, requestJSONObject.optString(Breezemoon.BREEZEMOON_CITY));

        try {
            return breezemoonRepository.add(bm);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Adds a breezemoon failed", e);

            throw new ServiceException(langPropsService.get("systemErrLabel"));
        }
    }

    /**
     * Updates a breezemoon with the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          "oId": "",
     *                          "breezemoonContent": "",
     *                          "breezemoonAuthorId": "",
     *                          "breezemoonIP": "",
     *                          "breezemoonUA": "",
     *                          "breezemoonStatus": "" // optional, 0 as default
     * @throws ServiceException service exception
     */
    @Transactional
    public void updateBreezemoon(final JSONObject requestJSONObject) throws ServiceException {
        requestJSONObject.put(Breezemoon.BREEZEMOON_CONTENT, ReservedWords.processReservedWord(requestJSONObject.optString(Breezemoon.BREEZEMOON_CONTENT)));
        final String content = requestJSONObject.optString(Breezemoon.BREEZEMOON_CONTENT);

        final String id = requestJSONObject.optString(Keys.OBJECT_ID);
        JSONObject old;
        try {
            old = breezemoonRepository.get(id);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Gets a breezemoon [id=" + id + "] failed", e);

            throw new ServiceException(langPropsService.get("systemErrLabel"));
        }

        if (null == old) {
            throw new ServiceException(langPropsService.get("queryFailedLabel"));
        }

        old.put(Breezemoon.BREEZEMOON_CONTENT, content);
        old.put(Breezemoon.BREEZEMOON_AUTHOR_ID, requestJSONObject.optString(Breezemoon.BREEZEMOON_AUTHOR_ID, old.optString(Breezemoon.BREEZEMOON_AUTHOR_ID)));
        old.put(Breezemoon.BREEZEMOON_IP, requestJSONObject.optString(Breezemoon.BREEZEMOON_IP));
        String ua = requestJSONObject.optString(Breezemoon.BREEZEMOON_UA);
        if (StringUtils.length(ua) > Common.MAX_LENGTH_UA) {
            ua = StringUtils.substring(ua, 0, Common.MAX_LENGTH_UA);
        }
        old.put(Breezemoon.BREEZEMOON_UA, ua);
        old.put(Breezemoon.BREEZEMOON_STATUS, requestJSONObject.optInt(Breezemoon.BREEZEMOON_STATUS, Breezemoon.BREEZEMOON_STATUS_C_VALID));
        final long now = System.currentTimeMillis();
        old.put(Breezemoon.BREEZEMOON_UPDATED, now);

        try {
            breezemoonRepository.update(id, old);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Updates a breezemoon failed", e);

            throw new ServiceException(langPropsService.get("systemErrLabel"));
        }
    }

    /**
     * Removes a breezemoon with the specified id.
     *
     * @param id the specified id
     * @throws ServiceException service exception
     */
    @Transactional
    public void removeBreezemoon(final String id) throws ServiceException {
        try {
            breezemoonRepository.remove(id);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Removes a breezemoon [id=" + id + "] failed", e);

            throw new ServiceException(langPropsService.get("systemErrLabel"));
        }
    }
}
