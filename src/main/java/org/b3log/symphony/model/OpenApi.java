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
package org.b3log.symphony.model;

/**
 * This class defines all report model relevant keys.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.2.0.0, Jul 15, 2018
 * @since 3.1.0
 */
public final class OpenApi {

    /**
     * OpenApi.
     */
    public static final String OPEN_API = "open_api";

    /**
     * Key of apply user name.
     */
    public static final String OPEN_API_T_USERNAME = "openApiApplyUserName";

    /**
     * OpenApi User Id
     */
     public static final String OPEN_API_USER_ID = "openApiUserId";

    /**
     * OpenApi Name
     */
    public static final String OPEN_API_NAME = "openApiName";

    /**
     * OpenApi Webhook address
     */
    public static final String OPEN_API_WEBHOOK_ADDRESS = "openApiWebhookAddress";

    /**
     * OpenApi Icon address
     */
    public static final String OPEN_API_ICON_ADDRESS = "openApiIcon";

     /**
     * OpenApi Key
     */
     public static final String OPEN_API_KEY = "openApiKey";

     /**
     * OpenApi Available
     */
     public static final String OPEN_API_AVAILABLE = "openApiAvailable";

     /**
     * OpenApi Rejected
     */
    public static final int OPEN_API_AVAILABLE_REJECTED = -1;

     /**
     * OpenApi Not Available
     */
     public static final int OPEN_API_AVAILABLE_NOT = 0;

     /**
     * OpenApi Available
     */
     public static final int OPEN_API_AVAILABLE_YES = 1;

     /**
     * OpenApi Update Time
     */
     public static final String OPEN_API_UPDATE_TIME = "openApiUpdateTime";

     /**
     * OpenApi Level
     */
     public static final String OPEN_API_LEVEL = "openApiLevel";

     /**
     * OpenApi Description
     */
     public static final String OPEN_API_DESCRIPTION = "openApiDescription";

     /**
     * OpenApi Type
     */
     public static final String OPEN_API_TYPE = "openApiType";

     /**
     * OpenApi Type:Game
     */
     public static final int OPEN_API_TYPE_GAME = 0;

     /**
     * OpenApi Type:App
     */
     public static final int OPEN_API_TYPE_APP = 1;

     /**
     * OpenApi Type:Other
     */
     public static final int OPEN_API_TYPE_OTHER = 2;






    /**
     * Private constructor.
     */
    private OpenApi() {
    }
}
