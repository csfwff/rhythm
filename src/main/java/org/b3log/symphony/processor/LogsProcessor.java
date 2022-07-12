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

import org.b3log.latke.Keys;
import org.b3log.latke.http.Dispatcher;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.http.renderer.AbstractFreeMarkerRenderer;
import org.b3log.latke.ioc.BeanManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.SortDirection;
import org.b3log.symphony.processor.middleware.LoginCheckMidware;
import org.b3log.symphony.repository.LogsRepository;
import org.b3log.symphony.service.DataModelService;
import org.b3log.symphony.util.StatusCodes;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;

@Singleton
public class LogsProcessor {

    /**
     * Data model service.
     */
    @Inject
    private DataModelService dataModelService;

    @Inject
    private LogsRepository logsRepository;

    /**
     * Register request handlers.
     */
    public static void register() {
        final BeanManager beanManager = BeanManager.getInstance();
        final LoginCheckMidware loginCheck = beanManager.getReference(LoginCheckMidware.class);

        final LogsProcessor logsProcessor = beanManager.getReference(LogsProcessor.class);
        Dispatcher.get("/logs", logsProcessor::showLogs, loginCheck::handle);
        Dispatcher.get("/logs/more", logsProcessor::moreLogs, loginCheck::handle);
    }

    public void moreLogs(final RequestContext context) {
        int page = 1;
        int pageSize = 20;
        try {
            page = Integer.parseInt(context.param("page"));
            pageSize = Integer.parseInt(context.param("pageSize"));
        } catch (Exception ignored) {
        }
        try {
            Query query = new Query()
                    .setFilter(new PropertyFilter("public", FilterOperator.EQUAL, true))
                    .addSort(Keys.OBJECT_ID, SortDirection.DESCENDING)
                    .setPage(page, pageSize);
            List<JSONObject> list = logsRepository.getList(query);
            context.renderJSON(StatusCodes.SUCC);
            context.renderData(list);
        } catch (Exception e) {
            context.renderJSON(StatusCodes.ERR);
            context.renderMsg("出现错误 " + e.getMessage());
        }
    }

    public void showLogs(final RequestContext context) {
        final AbstractFreeMarkerRenderer renderer = new SkinRenderer(context, "logs.ftl");
        final Map<String, Object> dataModel = renderer.getDataModel();

        dataModelService.fillHeaderAndFooter(context, dataModel);
        dataModelService.fillRandomArticles(dataModel);
        dataModelService.fillSideHotArticles(dataModel);
        dataModelService.fillSideTags(dataModel);
        dataModelService.fillLatestCmts(dataModel);
    }

}
