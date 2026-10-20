package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.ApiV3NotificationTypes;
import org.b3log.symphony.util.ApiV3WriteInputs;
import org.json.JSONObject;

/** Idempotent owned notification state writes. Emits no external events inside its transaction. */
@Service
public class ApiV3NotificationMgmtService {
    @Inject private NotificationStateMgmtService stateMgmtService;
    @Inject private ApiV3IdempotencyMgmtService idempotencyMgmtService;

    public JSONObject mark(final String userId, final String id, final String key) throws Exception {
        return idempotencyMgmtService.execute(userId, "notifications.read:" + id, key, new JSONObject(),
                () -> new JSONObject().put("changed", stateMgmtService.markReadInCurrentTransaction(userId, id)));
    }
    public JSONObject remove(final String userId, final String id, final String key) throws Exception {
        return idempotencyMgmtService.execute(userId, "notifications.delete:" + id, key, new JSONObject(),
                () -> new JSONObject().put("changed", stateMgmtService.removeInCurrentTransaction(userId, id)));
    }
    public JSONObject bulk(final String userId, final String category, final boolean remove, final String key) throws Exception {
        if (!ApiV3NotificationTypes.FILTERS.contains(category) || (remove && "all".equals(category))) throw ApiV3WriteInputs.invalid();
        final JSONObject body = new JSONObject().put("type", category);
        return idempotencyMgmtService.execute(userId, remove ? "notifications.delete-category" : "notifications.read-category", key, body,
                () -> new JSONObject().put("changed", stateMgmtService.bulkInCurrentTransaction(userId, category, remove)));
    }
}
