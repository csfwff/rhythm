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
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.service.LangPropsService;




import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.util.Crypts;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.OpenApi;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;

import org.b3log.symphony.service.*;
import org.b3log.symphony.util.Symphonys;
import org.b3log.symphony.util.Sessions;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;
import java.util.Map;

/**
 * Open Api processor.
 * <ul>
 * <li>show api index page</li>
 * <li>apply api</li>
 * </ul>
 *
 * @author <a href="">iwpz</a>
 */
@Singleton
public class OpenProcessor {

      /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ReportProcessor.class);


    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    /**
     * User query service.
     */
    @Inject
    private UserQueryService userQueryService;

    /**
     * Mgmt service.
     */
    @Inject
    private OpenApiMgmtService openApiMgmtService;

    /**
     * Language service.
     */
    @Inject
    private LangPropsService langPropsService;


    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheckMidware = beanManager.getReference(LoginCheckMidware.class);

        final OpenProcessor openProcessor = beanManager.getReference(OpenProcessor.class);
        Dispatcher.get("/open", openProcessor::showOpen, loginCheckMidware::handle);
        Dispatcher.get("/open/apply", openProcessor::showApply, loginCheckMidware::handle);
        Dispatcher.post("/open/apply", openProcessor::apply, loginCheckMidware::handle);
    }

    /**
     * Shows Open Platform index.
     *
     * @param context the specified context
     */
    public void showOpen(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "open/index.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModel.put(Common.SELECTED, Common.TOP);

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

    /**
     * Shows apply apiKey page
     *
     * @param context the specified context
     */
    public void showApply(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "open/apply.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();
        dataModelService.fillHeaderAndFooter(context, dataModel);
    }

    /**
     * apply a apiKey
     *
     * @param context the specified context
     */
    public void apply(final RequestContext context) {
      context.renderJSON(StatusCodes.ERR);
      final JSONObject requestJSONObject = context.requestJSON();

        JSONObject currentUser = Sessions.getUser();
        try {
            currentUser = ApiProcessor.getUserByKey(requestJSONObject.optString("apiKey"));
        } catch (NullPointerException ignored) {
        }
        final String userId = currentUser.optString(Keys.OBJECT_ID);
        final int type = requestJSONObject.optInt(OpenApi.OPEN_API_TYPE);
        final long currentTimeMillis = System.currentTimeMillis();
        final String description = StringUtils.trim(requestJSONObject.optString(OpenApi.OPEN_API_DESCRIPTION));
        final String openApi = Crypts.encryptByAES(userId + String.valueOf(currentTimeMillis), Symphonys.COOKIE_SECRET);

        final JSONObject openApiModel = new JSONObject();
        openApiModel.put(OpenApi.OPEN_API_USER_ID, userId);
        openApiModel.put(OpenApi.OPEN_API_TYPE, type);
        openApiModel.put(OpenApi.OPEN_API_DESCRIPTION, description);
        openApiModel.put(OpenApi.OPEN_API_KEY, openApi);
        openApiModel.put(OpenApi.OPEN_API_UPDATE_TIME, currentTimeMillis);
      

        try {
            openApiMgmtService.addOpenApi(openApiModel);

            context.renderJSONValue(Keys.CODE, StatusCodes.SUCC);
        } catch (final ServiceException e) {
            context.renderMsg(e.getMessage());
            context.renderJSONValue(Keys.CODE, StatusCodes.ERR);
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Apply a apiKey failed", e);

            context.renderMsg(langPropsService.get("systemErrLabel"));
            context.renderJSONValue(Keys.CODE, StatusCodes.ERR);
        }

    }
}
