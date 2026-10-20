package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.LivenessRewardClaim;
import org.json.JSONObject;

@Service
public class ApiV3ActivityMgmtService {
    @Inject private LivenessRewardMgmtService rewards;
    @Inject private ApiV3IdempotencyMgmtService idempotency;
    public JSONObject status(final String actor) throws Exception { return rewards.status(actor); }
    public JSONObject claim(final String actor, final String key) throws Exception {
        return rewards.withTransferLock(actor, () -> {
            final LivenessRewardClaim.Result[] committed = new LivenessRewardClaim.Result[1];
            final var execution = idempotency.executeWithOutcome(actor, "activity.yesterday-reward", key, new JSONObject(), () -> {
                try {
                    committed[0] = rewards.claimInCurrentTransaction(actor);
                    return committed[0].dto();
                } catch (final LivenessRewardClaim.Unavailable e) {
                    throw new ApiV3Exception(409, "reward_unavailable", "当前没有可领取的昨日活跃度奖励");
                }
            });
            if (execution.applied()) rewards.afterCommit(actor, committed[0]);
            return execution.result();
        });
    }
}
