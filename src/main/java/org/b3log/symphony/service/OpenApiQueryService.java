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
import org.b3log.latke.Latkes;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.Pagination;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.LangPropsService;
import org.b3log.latke.service.annotation.Service;
import org.b3log.latke.util.Paginator;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.OpenApi;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.OpenApiRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.Emotions;
import org.b3log.symphony.util.Escapes;
import org.b3log.symphony.util.Markdowns;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Report management service.
 *
 * @author <a href="http://88250.b3log.org">Liang Ding</a>
 * @version 1.0.0.2, Jun 27, 2018
 * @since 3.1.0
 */
@Service
public class OpenApiQueryService {

  /**
   * Logger.
   */
  private static final Logger LOGGER = LogManager.getLogger(OpenApiQueryService.class);

  /**
   * Report repository.
   */
  @Inject
  private OpenApiRepository openApiRepository;

  /**
   * User repository.
   */
  @Inject
  private UserRepository userRepository;

  /**
   * Article repository.
   */
  @Inject
  private ArticleRepository articleRepository;

  /**
   * Comment repository.
   */
  @Inject
  private CommentRepository commentRepository;

  /**
   * Language service.
   */
  @Inject
  private LangPropsService langPropsService;

  /**
   * Comment query service.
   */
  @Inject
  private CommentQueryService commentQueryService;

  /**
   * Chat Room Service.
   */
  @Inject
  private ChatRoomService chatRoomService;

  /**
     * Gets report by the specified request json object.
     *
     * @param requestJSONObject the specified request json object, for example,
     *                          {
     *                          "paginationCurrentPageNum": 1,
     *                          "paginationPageSize": 20,
     *                          "paginationWindowSize": 10
     *                          }, see {@link Pagination} for more details
     * @return for example,      <pre>
     * {
     *     "pagination": {
     *         "paginationPageCount": 100,
     *         "paginationPageNums": [1, 2, 3, 4, 5]
     *     },
     *     "reports": [{
     *         "oId": "",
     *         "reportUserName": "<a>/member/username</a>",
     *         "reportData": "<a>Article or user</a>",
     *         "reportDataType": int,
     *         "reportDataTypeStr": "",
     *         "reportType": int,
     *         "reportTypeStr": "",
     *         "reportMemo": "",
     *         "reportHandled": int,
     *
     *      }, ....]
     * }
     * </pre>
     * @see Pagination
     */
    public JSONObject getOpenApis(final JSONObject requestJSONObject) {
        final JSONObject ret = new JSONObject();

        final int currentPageNum = requestJSONObject.optInt(Pagination.PAGINATION_CURRENT_PAGE_NUM);
        final int pageSize = requestJSONObject.optInt(Pagination.PAGINATION_PAGE_SIZE);
        final int windowSize = requestJSONObject.optInt(Pagination.PAGINATION_WINDOW_SIZE);
        final Query query = new Query().setPage(currentPageNum, pageSize).
                addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
        JSONObject result;
        try {
            result = openApiRepository.get(query);
        } catch (final RepositoryException e) {
            LOGGER.log(Level.ERROR, "Get reports failed", e);
            return null;
        }

        final int pageCount = result.optJSONObject(Pagination.PAGINATION).optInt(Pagination.PAGINATION_PAGE_COUNT);
        final JSONObject pagination = new JSONObject();
        ret.put(Pagination.PAGINATION, pagination);
        final List<Integer> pageNums = Paginator.paginate(currentPageNum, pageSize, pageCount, windowSize);
        pagination.put(Pagination.PAGINATION_PAGE_COUNT, pageCount);
        pagination.put(Pagination.PAGINATION_PAGE_NUMS, pageNums);

        final List<JSONObject> records = (List<JSONObject>) result.opt(Keys.RESULTS);
        final List<JSONObject> openApis = new ArrayList<>();
        try{
          for (final JSONObject record : records) {
              final JSONObject openapi = new JSONObject();        
                  final String openApiUserId = record.optString(OpenApi.OPEN_API_USER_ID);
                  final JSONObject OpenApiApplier = userRepository.get(openApiUserId);
                  openapi.put(OpenApi.OPEN_API_T_USERNAME, UserExt.getUserLink(OpenApiApplier));
                  openapi.put(OpenApi.OPEN_API_AVAILABLE,record.optInt(OpenApi.OPEN_API_AVAILABLE));
                  openapi.put(OpenApi.OPEN_API_UPDATE_TIME, OpenApi.OPEN_API_UPDATE_TIME);
                  openapi.put("oId",record.optString("oId"));
                  openapi.put(OpenApi.OPEN_API_LEVEL,record.optInt(OpenApi.OPEN_API_LEVEL));
                  openapi.put(OpenApi.OPEN_API_TYPE,record.optInt(OpenApi.OPEN_API_TYPE));
                  openapi.put(OpenApi.OPEN_API_NAME, record.optString(OpenApi.OPEN_API_NAME));
                  openapi.put(OpenApi.OPEN_API_DESCRIPTION, record.optString(OpenApi.OPEN_API_DESCRIPTION));
                  openApis.add(openapi);
              } 
            }
            catch (final Exception e) {
                LOGGER.log(Level.ERROR, "Builds report data failed", e);
            }
            ret.put(OpenApi.OPEN_API, (Object) openApis);
            return ret;
          }
}
