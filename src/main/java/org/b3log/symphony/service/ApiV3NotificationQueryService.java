package org.b3log.symphony.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Notification;
import org.b3log.symphony.model.Reward;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.NotificationRepository;
import org.b3log.symphony.repository.RewardRepository;
import org.b3log.symphony.util.ApiV3NotificationTypes;
import org.b3log.symphony.util.ApiV3NotificationDto;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.Emotions;
import org.b3log.symphony.util.Escapes;
import org.b3log.symphony.util.Markdowns;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

/** Owned notification DTOs. Related content is resolved with the explicit Bearer viewer. */
@Service
public class ApiV3NotificationQueryService {
    private static final Logger LOGGER = LogManager.getLogger(ApiV3NotificationQueryService.class);
    @Inject private NotificationRepository notificationRepository;
    @Inject private CommentRepository commentRepository;
    @Inject private CommentQueryService commentQueryService;
    @Inject private RewardRepository rewardRepository;
    @Inject private NotificationQueryService notificationQueryService;
    @Inject private ApiV3ContentQueryService contentQueryService;
    @Inject private ApiV3ContentDtoService contentDtoService;
    @Inject private UserQueryService userQueryService;
    @Inject private ChatRoomService chatRoomService;

    public JSONObject list(final JSONObject viewer, final String type, final String status, final int p, final int size) throws RepositoryException {
        final String userId = viewer.optString(Keys.OBJECT_ID);
        final JSONArray list = new JSONArray();
        for (final JSONObject row : notificationRepository.getV3Page(userId, type, status, p, size)) list.put(dto(row, viewer));
        return ApiV3Requests.pagination(list, p, size, notificationRepository.countV3(userId, type, status));
    }
    public JSONObject get(final JSONObject viewer, final String id) throws RepositoryException {
        final JSONObject row = notificationRepository.getOwned(viewer.optString(Keys.OBJECT_ID), id);
        if (null == row) throw ApiV3Requests.notFound();
        return dto(row, viewer);
    }
    public JSONObject unread(final String userId) throws RepositoryException {
        final JSONObject categories = new JSONObject();
        for (final String category : ApiV3NotificationTypes.CATEGORIES.keySet()) categories.put(category, 0L);
        categories.put("other", 0L);
        long total = 0;
        for (final JSONObject row : notificationRepository.countV3Unread(userId)) {
            final String category = ApiV3NotificationTypes.category(row.optInt(Notification.NOTIFICATION_DATA_TYPE));
            final long count = row.optLong("total");
            categories.put(category, categories.optLong(category) + count);
            total += count;
        }
        return new JSONObject().put("total", total).put("categories", categories);
    }

    private JSONObject dto(final JSONObject row, final JSONObject viewer) throws RepositoryException {
        final int type = row.optInt(Notification.NOTIFICATION_DATA_TYPE);
        final JSONObject dto = ApiV3NotificationDto.base(row);
        final String dataId = row.optString(Notification.NOTIFICATION_DATA_ID);
        try {
            if ("point".equals(dto.optString("category"))) {
                if (!pointTargetVisible(row, viewer)) return unavailable(dto);
                final String html = notificationQueryService.getPointNotificationDescription(row, viewer.optString(Keys.OBJECT_ID));
                if (null == html) return unavailable(dto);
                description(dto, html);
            } else if ("sys-announce".equals(dto.optString("category"))) {
                if (type == Notification.DATA_TYPE_C_SYS_ANNOUNCE_ARTICLE && !article(dto, dataId, viewer)) return unavailable(dto);
                description(dto, notificationQueryService.getSysAnnouncementDescription(row));
            } else if (type == Notification.DATA_TYPE_C_CHAT_ROOM_AT || type == Notification.DATA_TYPE_C_RED_PACKET) {
                chat(dto, dataId, type);
            } else if (type == Notification.DATA_TYPE_C_NEW_FOLLOWER) {
                final JSONObject actor = userQueryService.getUser(dataId);
                if (null == actor) return unavailable(dto);
                dto.put("actor", contentDtoService.author(actor, false));
                dto.put("url", "/member/" + actor.optString(User.USER_NAME));
            } else if (type == Notification.DATA_TYPE_C_ARTICLE_NEW_FOLLOWER || type == Notification.DATA_TYPE_C_ARTICLE_NEW_WATCHER
                    || type == Notification.DATA_TYPE_C_COMMENT_VOTE_UP || type == Notification.DATA_TYPE_C_COMMENT_VOTE_DOWN
                    || type == Notification.DATA_TYPE_C_ARTICLE_VOTE_UP || type == Notification.DATA_TYPE_C_ARTICLE_VOTE_DOWN) {
                final String[] parts = dataId.split("-", 2);
                if (parts.length != 2) return unavailable(dto);
                final boolean comment = type == Notification.DATA_TYPE_C_COMMENT_VOTE_UP || type == Notification.DATA_TYPE_C_COMMENT_VOTE_DOWN;
                if (!(comment ? comment(dto, parts[0], viewer, false) : article(dto, parts[0], viewer))) return unavailable(dto);
                final JSONObject actor = userQueryService.getUser(parts[1]);
                if (null != actor) dto.put("actor", contentDtoService.author(actor, false));
            } else if (type == Notification.DATA_TYPE_C_COMMENT || type == Notification.DATA_TYPE_C_COMMENTED || type == Notification.DATA_TYPE_C_REPLY
                    || type == Notification.DATA_TYPE_C_FOLLOWING_ARTICLE_COMMENT) {
                if (!comment(dto, dataId, viewer, true)) return unavailable(dto);
            } else if (type == Notification.DATA_TYPE_C_AT) {
                final JSONObject comment = commentRepository.get(dataId);
                if (null != comment) {
                    if (!comment(dto, dataId, viewer, true)) return unavailable(dto);
                } else if (!article(dto, dataId, viewer)) return unavailable(dto);
            } else if ("broadcast".equals(dto.optString("category")) || "following".equals(dto.optString("category"))) {
                if (!article(dto, dataId, viewer)) return unavailable(dto);
            } else return unavailable(dto);
        } catch (final JSONException | NullPointerException | IndexOutOfBoundsException e) {
            // Historical notifications can outlive their target or contain old, malformed references.
            LOGGER.warn("Cannot render legacy notification id={}", row.optString(Keys.OBJECT_ID));
            return unavailable(dto);
        }
        if (dto.optString("descriptionHtml").isEmpty()) description(dto, dto.optString("title"));
        return dto;
    }

    private boolean article(final JSONObject dto, final String id, final JSONObject viewer) throws RepositoryException {
        final JSONObject article = contentQueryService.viewableArticle(id, viewer);
        if (null == article) return false;
        dto.put("article", contentDtoService.summary(article)).put("url", article.optString(Article.ARTICLE_PERMALINK));
        return true;
    }

    private boolean comment(final JSONObject dto, final String id, final JSONObject viewer, final boolean includeContent) throws RepositoryException {
        final JSONObject comment = commentRepository.get(id);
        if (null == comment || Comment.COMMENT_STATUS_C_VALID != comment.optInt(Comment.COMMENT_STATUS)) return false;
        final String articleId = comment.optString(Comment.COMMENT_ON_ARTICLE_ID);
        final JSONObject article = contentQueryService.viewableArticle(articleId, viewer);
        if (null == article) return false;
        final JSONObject publicComment = contentQueryService.commentDto(comment, article, viewer);
        dto.put("article", contentDtoService.summary(article)).put("comment", publicComment)
                .put("url", article.optString(Article.ARTICLE_PERMALINK) + "#" + comment.optString(Keys.OBJECT_ID));
        final int mode = viewer.optInt(UserExt.USER_COMMENT_VIEW_MODE) == UserExt.USER_COMMENT_VIEW_MODE_C_REALTIME
                ? UserExt.USER_COMMENT_VIEW_MODE_C_REALTIME : UserExt.USER_COMMENT_VIEW_MODE_C_TRADITIONAL;
        final String target = commentQueryService.getCommentURL(id, mode, Symphonys.ARTICLE_COMMENTS_CNT);
        if (null != target) {
            final var anchor = Jsoup.parseBodyFragment(target).selectFirst("a[href]");
            if (null != anchor) dto.put("url", anchor.attr("href"));
        }
        if (includeContent && publicComment.optBoolean("contentVisible")) description(dto, publicComment.optString("contentHtml"));
        return true;
    }

    private boolean pointTargetVisible(final JSONObject row, final JSONObject viewer) throws RepositoryException {
        final int type = row.optInt(Notification.NOTIFICATION_DATA_TYPE);
        final String dataId = row.optString(Notification.NOTIFICATION_DATA_ID);
        if (type == Notification.DATA_TYPE_C_POINT_PERFECT_ARTICLE) return null != contentQueryService.viewableArticle(dataId, viewer);
        if (type == Notification.DATA_TYPE_C_POINT_ARTICLE_THANK || type == Notification.DATA_TYPE_C_POINT_ARTICLE_REWARD
                || type == Notification.DATA_TYPE_C_POINT_COMMENT_ACCEPT || type == Notification.DATA_TYPE_C_POINT_COMMENT_THANK) {
            final JSONObject reward = rewardRepository.get(dataId);
            if (null == reward) return false;
            String id = reward.optString(Reward.DATA_ID);
            if (type == Notification.DATA_TYPE_C_POINT_COMMENT_THANK) {
                final JSONObject comment = commentRepository.get(id);
                if (null == comment) return false;
                id = comment.optString(Comment.COMMENT_ON_ARTICLE_ID);
            }
            return null != contentQueryService.viewableArticle(id, viewer);
        }
        return true;
    }

    private void chat(final JSONObject dto, final String id, final int type) {
        final JSONObject row = chatRoomService.getChatMsg(id);
        if (null == row) { unavailable(dto); return; }
        final JSONObject message = new JSONObject(row.optString(Common.CONTENT));
        String text = message.optString(Common.CONTENT);
        boolean sourceText = type == Notification.DATA_TYPE_C_RED_PACKET;
        if (sourceText) text = new JSONObject(text).optString("msg");
        else if (text.stripLeading().startsWith("{")) {
            JSONObject envelope = null;
            try { envelope = new JSONObject(text); } catch (final JSONException ignored) { }
            if (null != envelope && !envelope.optString("msgType").isEmpty()) {
                text = envelope.optString("msg", dto.optString("title"));
                sourceText = true;
            }
        }
        description(dto, sourceText ? Escapes.escapeHTML(text) : text);
        final JSONObject actor = userQueryService.getUserByName(message.optString(User.USER_NAME));
        if (null != actor) dto.put("actor", contentDtoService.author(actor, false));
        dto.put("url", "/cr");
    }

    private static void description(final JSONObject dto, final String html) {
        final String safe = Markdowns.clean(Emotions.convert(html), "");
        dto.put("descriptionHtml", safe).put("description", Jsoup.parse(safe).text());
    }
    private static JSONObject unavailable(final JSONObject dto) {
        return ApiV3NotificationDto.unavailable(dto);
    }
}
