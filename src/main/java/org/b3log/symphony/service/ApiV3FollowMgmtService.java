package org.b3log.symphony.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Follow;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Permission;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.util.ApiV3ContentUtil;
import org.b3log.symphony.util.ApiV3Requests;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

@Service
public class ApiV3FollowMgmtService {
    private static final Logger LOGGER = LogManager.getLogger(ApiV3FollowMgmtService.class);
    @Inject private FollowMgmtService follows;
    @Inject private ApiV3IdempotencyMgmtService idempotency;
    @Inject private NotificationMgmtService notifications;
    @Inject private UserQueryService users;
    @Inject private RoleQueryService roles;

    public JSONObject set(final JSONObject viewer, final String targetId, final int type, final boolean enabled, final String key) throws Exception {
        final String actor = viewer.optString(Keys.OBJECT_ID);
        if (enabled && (type == Follow.FOLLOWING_TYPE_C_ARTICLE || type == Follow.FOLLOWING_TYPE_C_ARTICLE_WATCH)) {
            final String permission = type == Follow.FOLLOWING_TYPE_C_ARTICLE ? Permission.PERMISSION_ID_C_COMMON_FOLLOW_ARTICLE : Permission.PERMISSION_ID_C_COMMON_WATCH_ARTICLE;
            if (!Permission.hasPermission(Set.of(permission), roles.getPermissions(viewer.optString(User.USER_ROLE)))) throw ApiV3Requests.forbidden();
        }
        try {
            final JSONObject stored = idempotency.execute(actor, "follows.set:" + type + ":" + targetId, key, new JSONObject().put("following", enabled), () -> {
                final JSONObject locked = new JSONObject();
                final boolean changed = follows.setInCurrentTransaction(actor, targetId, type, enabled, target -> {
                    validate(target, type, viewer);
                    locked.put("target", target);
                });
                String recipient = "";
                if (enabled && changed && type != Follow.FOLLOWING_TYPE_C_TAG) {
                    final JSONObject target = locked.getJSONObject("target");
                    recipient = type == Follow.FOLLOWING_TYPE_C_USER ? targetId : target.optString(Article.ARTICLE_AUTHOR_ID);
                    if (!actor.equals(recipient)) {
                        final int notificationType = type == Follow.FOLLOWING_TYPE_C_USER ? Notification.DATA_TYPE_C_NEW_FOLLOWER
                                : type == Follow.FOLLOWING_TYPE_C_ARTICLE ? Notification.DATA_TYPE_C_ARTICLE_NEW_FOLLOWER : Notification.DATA_TYPE_C_ARTICLE_NEW_WATCHER;
                        notifications.addFollowNotificationInCurrentTransaction(recipient, type == Follow.FOLLOWING_TYPE_C_USER ? actor : targetId + "-" + actor, notificationType);
                    } else recipient = "";
                }
                return new JSONObject().put("following", enabled).put("changed", changed).put("notifyUser", recipient);
            });
            final String recipient = stored.optString("notifyUser");
            if (!recipient.isEmpty()) try { notifications.publishRefreshNotification(recipient); }
            catch (final Exception e) { LOGGER.warn("Failed to refresh committed v3 follow notification"); }
            return new JSONObject().put("following", stored.getBoolean("following")).put("changed", stored.getBoolean("changed"));
        } finally {
            // Repository updates populate caches before commit. Evict after both commit and rollback.
            follows.invalidateCaches(targetId, type);
        }
    }
    private void validate(final JSONObject target, final int type, final JSONObject viewer) throws RepositoryException {
        if (null == target) throw ApiV3Requests.notFound();
        if (type == Follow.FOLLOWING_TYPE_C_USER) {
            if (UserExt.USER_STATUS_C_VALID != target.optInt(UserExt.USER_STATUS)) throw ApiV3Requests.notFound();
        } else if (type == Follow.FOLLOWING_TYPE_C_TAG) {
            if (Tag.TAG_STATUS_C_VALID != target.optInt(Tag.TAG_STATUS)) throw ApiV3Requests.notFound();
        } else {
            final Set<String> invited = Article.ARTICLE_TYPE_C_DISCUSSION == target.optInt(Article.ARTICLE_TYPE)
                    ? users.getUserNames(target.optString(Article.ARTICLE_CONTENT)) : Collections.emptySet();
            if (!ApiV3ContentUtil.canViewArticle(target, viewer, invited)) throw ApiV3Requests.notFound();
            final JSONObject author = users.getUser(target.optString(Article.ARTICLE_AUTHOR_ID));
            if (null == author || UserExt.USER_STATUS_C_INVALID == author.optInt(UserExt.USER_STATUS)
                    || UserExt.USER_STATUS_C_DEACTIVATED == author.optInt(UserExt.USER_STATUS)) throw ApiV3Requests.notFound();
        }
    }
}
