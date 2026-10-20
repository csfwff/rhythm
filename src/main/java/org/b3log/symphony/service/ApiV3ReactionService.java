package org.b3log.symphony.service;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.ApiV3ReactionDto;
import org.json.JSONObject;

@Service
public class ApiV3ReactionService {
    @Inject private ApiV3ReactionTargetService targets;
    @Inject private ReactionQueryService query;
    @Inject private ReactionMgmtService management;
    @Inject private ApiV3IdempotencyMgmtService idempotency;
    @Inject private ApiV3ReactionEventService events;
    public JSONObject read(final JSONObject viewer, final String type, final String id) throws Exception {
        targets.read(viewer, type, id);
        return ApiV3ReactionDto.from(query.buildOperationResultOrThrow(type, id, null == viewer ? "" : viewer.optString(Keys.OBJECT_ID)));
    }
    public JSONObject set(final JSONObject viewer, final String type, final String id, final String value, final String key) throws Exception {
        final String actor = viewer.optString(Keys.OBJECT_ID);
        final ApiV3IdempotencyMgmtService.Execution execution = idempotency.executeWithOutcome(actor, "reactions.set:" + type + ":" + id,
                key, new JSONObject().put("value", value), () -> {
                    final boolean changed = management.setInCurrentTransaction(actor, type, id, value,
                            target -> targets.validate(viewer, type, target, true));
                    return ApiV3ReactionDto.from(query.buildOperationResultOrThrow(type, id, actor)).put("changed", changed);
                });
        if (execution.applied() && execution.result().optBoolean("changed")) events.publish(viewer, type, id);
        return execution.result();
    }
}
