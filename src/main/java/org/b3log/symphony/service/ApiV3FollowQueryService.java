package org.b3log.symphony.service;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.CompositeFilterOperator;
import org.b3log.latke.repository.Filter;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.SortDirection;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Follow;
import org.b3log.symphony.repository.FollowRepository;
import org.b3log.symphony.util.ApiV3Requests;
import org.json.JSONArray;
import org.json.JSONObject;

/** Owned relationships keep inaccessible targets as placeholders, without disclosing target metadata. */
@Service
public class ApiV3FollowQueryService {
    @Inject private FollowRepository follows;
    @Inject private ApiV3ContentQueryService content;
    @Inject private ApiV3ContentDtoService dto;
    public JSONObject articles(final JSONObject viewer, final boolean watch, final int p, final int size) throws RepositoryException {
        final Filter filter = CompositeFilterOperator.and(
                new PropertyFilter(Follow.FOLLOWER_ID, FilterOperator.EQUAL, viewer.optString(Keys.OBJECT_ID)),
                new PropertyFilter(Follow.FOLLOWING_TYPE, FilterOperator.EQUAL, watch ? Follow.FOLLOWING_TYPE_C_ARTICLE_WATCH : Follow.FOLLOWING_TYPE_C_ARTICLE));
        final Query query = new Query().setFilter(filter).setPage(p, size).setPageCount(1).addSort(Keys.OBJECT_ID, SortDirection.DESCENDING);
        final JSONArray list = new JSONArray();
        for (final JSONObject relation : follows.getList(query)) {
            final String id = relation.optString(Follow.FOLLOWING_ID);
            final JSONObject article = content.viewableArticle(id, viewer);
            list.put(null == article ? new JSONObject().put("id", id).put("available", false) : dto.summary(article).put("available", true));
        }
        return ApiV3Requests.pagination(list, p, size, follows.count(new Query().setFilter(filter)));
    }
}
