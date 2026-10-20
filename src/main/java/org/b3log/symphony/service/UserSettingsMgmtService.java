package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.Transaction;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.censor.CensorFactory;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.ApiV3Settings;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.Escapes;
import org.b3log.symphony.util.ProfileWriteQuota;
import org.b3log.symphony.util.UserSettingsMutation;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;

/** Current-transaction settings primitive; no nested transactions or cache/session publication. */
@Service
public class UserSettingsMgmtService {
    @Inject private UserRepository users;
    @Inject private SkinQueryService skins;
    public JSONObject read(final String actor, final String group) throws RepositoryException {
        final var rows = users.select("SELECT * FROM " + users.getName() + " WHERE oId=?", actor);
        if (rows.isEmpty()) throw ApiV3Requests.notFound();
        return ApiV3Settings.dto(group, rows.getFirst());
    }
    public JSONObject updateInCurrentTransaction(final String actor, final String group, final JSONObject requested, final boolean quota) throws RepositoryException {
        return UserSettingsMutation.apply(new UserSettingsMutation.Store() {
            public boolean hasTransaction() { return users.hasTransactionBegun(); }
            public JSONObject lockOwner(final String id) throws RepositoryException { return users.getForUpdate(id); }
            public String canonicalSkin(final String skin, final String device) {
                final String canonical = skins.canonicalizeSkin(skin, device);
                if (!skins.isValidSkin(canonical, device)) throw new ApiV3Exception(422, "invalid_skin", "主题不存在或设备类型不匹配");
                return canonical;
            }
            public String escapeText(final String text) { return Escapes.escapeHTML(text); }
            public void validatePersonality(final String id, final boolean quota) {
                if (quota && !ProfileWriteQuota.access(id)) throw new ApiV3Exception(429, "too_many_requests", "操作过于频繁，请稍后再试");
            }
            public void validateProfile(final String id, final JSONObject merged, final boolean quota) {
                if (quota && !ProfileWriteQuota.access(id)) throw new ApiV3Exception(429, "too_many_requests", "操作过于频繁，请稍后再试");
                if (CensorFactory.getTextCensor().censor(merged.optString("userNickname") + "\n" + merged.optString("userIntro") + "\n" + merged.optString("userTags")).isBlocked()
                        || CensorFactory.getTextCensor().censor(merged.optString("userURL")).isBlocked()) throw new ApiV3Exception(422, "content_rejected", "个人信息中含违规内容");
            }
            public void update(final String id, final JSONObject values) throws RepositoryException { users.updateSettingsInCurrentTransaction(id, values); }
        }, actor, group, requested, quota, System.currentTimeMillis());
    }

    public void updateLegacy(final String actor, final String group, final JSONObject changes) throws RepositoryException {
        updateLegacy(actor, java.util.Map.of(group, changes));
    }
    public void updateLegacy(final String actor, final java.util.Map<String, JSONObject> groups) throws RepositoryException {
        final Transaction tx = users.beginTransaction();
        try {
            for (final var group : groups.entrySet()) updateInCurrentTransaction(actor, group.getKey(), group.getValue(), false);
            tx.commit();
        } catch (final RepositoryException | RuntimeException e) { if (tx.isActive()) tx.rollback(); throw e; }
        finally { refresh(actor); }
    }
    public void refresh(final String actor) {
        try { users.refreshSettingsCache(actor); }
        catch (final Exception e) {
            users.evictSettingsCaches(actor);
            LogManager.getLogger(UserSettingsMgmtService.class).error("Refresh durable settings cache failed", e);
        } finally { ApiV3RankingQueryService.invalidateSettings(); }
    }
}
