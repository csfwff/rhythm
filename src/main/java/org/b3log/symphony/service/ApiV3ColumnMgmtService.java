package org.b3log.symphony.service;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.ServiceException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.LongArticleColumn;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.ApiV3Requests;
import org.json.JSONObject;
import java.util.List;

@Service
public class ApiV3ColumnMgmtService {
    @Inject private LongArticleColumnMgmtService columns;
    @Inject private ColumnCoverMgmtService covers;
    @Inject private ApiV3IdempotencyMgmtService idempotency;

    public JSONObject title(final String actor, final String id, final String title, final String key) throws Exception {
        return idempotency.execute(actor, "columns.title:" + id, key, new JSONObject().put("title", title), () -> {
            try { return dto(columns.renameColumnInCurrentTransaction(actor, id, title)); }
            catch (final ServiceException e) { throw translate(e); }
        });
    }
    public JSONObject cover(final String actor, final String id, final String url, final String key) throws Exception {
        return idempotency.execute(actor, "columns.cover:" + id, key, new JSONObject().put("coverURL", url), () -> {
            try { return dto(covers.updateOwnedCoverURLInCurrentTransaction(actor, id, url)); }
            catch (final ServiceException e) {
                if (e.getCause() instanceof RepositoryException repository) throw repository;
                throw translate(e);
            }
        });
    }
    public JSONObject order(final String actor, final List<String> ids, final String key) throws Exception {
        return idempotency.execute(actor, "columns.order", key, new JSONObject().put("columnIds", ids), () -> {
            try { columns.updateColumnOrderInCurrentTransaction(actor, ids); return new JSONObject().put("columnIds", ids); }
            catch (final ServiceException e) { throw translate(e); }
        });
    }
    public JSONObject remove(final String actor, final String id, final String key) throws Exception {
        return idempotency.execute(actor, "columns.remove:" + id, key, new JSONObject(), () -> {
            try { columns.removeColumnInCurrentTransaction(actor, id); return new JSONObject().put("id", id).put("deleted", true); }
            catch (final ServiceException e) { throw translate(e); }
        });
    }
    private static ApiV3Exception translate(final ServiceException error) {
        if ("专栏不存在或无权限操作".equals(error.getMessage())) return ApiV3Requests.notFound();
        if ("已存在同名专栏".equals(error.getMessage()) || "专栏下还有章节，请先移出章节后再删除".equals(error.getMessage())) {
            return new ApiV3Exception(409, "column_conflict", error.getMessage());
        }
        return new ApiV3Exception(422, "invalid_request", "请求内容不符合要求");
    }
    public static JSONObject dto(final JSONObject column) {
        return new JSONObject().put("id", column.optString(Keys.OBJECT_ID))
                .put("title", column.optString(LongArticleColumn.COLUMN_TITLE)).put("order", column.optInt(LongArticleColumn.COLUMN_ORDER))
                .put("coverURL", column.optString(LongArticleColumn.COLUMN_COVER_URL)).put("articleCount", column.optInt(LongArticleColumn.COLUMN_ARTICLE_COUNT))
                .put("createdAt", column.optLong(LongArticleColumn.COLUMN_CREATE_TIME)).put("updatedAt", column.optLong(LongArticleColumn.COLUMN_UPDATE_TIME));
    }
}
