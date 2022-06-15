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
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.repository.annotation.Transactional;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Pointtransfer;
import org.b3log.symphony.model.OpenApi;
import org.b3log.symphony.repository.OpenApiRepository;
import org.json.JSONObject;

/**
 * Report management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, Jul 15, 2018
 * @since 3.1.0
 */
@Service
public class OpenApiMgmtService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(OpenApiMgmtService.class);

    /**
     * Report repository.
     */
    @Inject
    private OpenApiRepository openApiRepository;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;

    /**
     * Pointtransfer management service.
     */
    @Inject
    private PointtransferMgmtService pointtransferMgmtService;

    /**
     * Notification management service.
     */
    @Inject
    private NotificationMgmtService notificationMgmtService;

    /**
     * Adds a OpenApi.
     *
     * @param report the specified openApi model, for example,
     *               {
     *               "openApiUserId": "",
     *               "openApiKey": "",
     *               "openApiAvailable": int,
     *               "openApiUpdateTime": ,
     *               "openApiLevel": int,
     *               "openApiDescription":"",
     *               }
     * @throws ServiceException service exception
     */
    @Transactional
    public void addOpenApi(final JSONObject openApi) throws ServiceException {
      openApi.put(OpenApi.OPEN_API_AVAILABLE, OpenApi.OPEN_API_AVAILABLE_NOT);

        try {
          openApiRepository.add(openApi);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Adds a openApi failed", e);

            throw new ServiceException(langPropsService.get("systemErrLabel"));
        }
    }
}
