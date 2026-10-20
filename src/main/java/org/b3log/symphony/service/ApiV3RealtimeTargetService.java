package org.b3log.symphony.service;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.model.User;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.ApiV3ContentUtil;
import org.b3log.symphony.util.UserMentionCandidates;
import org.json.JSONObject;
import java.util.Set;

/** Current database permissions for long-lived subscriptions; never page sessions or cached entities. */
@Service
public class ApiV3RealtimeTargetService {
    @Inject private ArticleRepository articles;
    @Inject private UserRepository users;

    public boolean articleVisible(final String actor, final String articleId) throws RepositoryException {
        if (actor == null || !actor.matches("[0-9]{1,32}") || articleId == null || !articleId.matches("[0-9]{1,32}")) return false;
        final var rows = articles.select("SELECT a.oId,a.articleAuthorId,a.articleType,a.articleStatus,a.articleAnonymousView,"
                + "CASE WHEN a.articleType=? THEN a.articleContent ELSE '' END AS articleContent,"
                + "v.oId AS viewerId,v.userName AS viewerName,v.userRole AS viewerRole FROM `" + articles.getName()
                + "` a JOIN `" + users.getName() + "` u ON u.oId=a.articleAuthorId JOIN `" + users.getName()
                + "` v ON v.oId=? WHERE a.oId=? AND u.userStatus NOT IN (?,?) AND v.userStatus=? LIMIT 1",
                Article.ARTICLE_TYPE_C_DISCUSSION, actor, articleId, UserExt.USER_STATUS_C_INVALID,
                UserExt.USER_STATUS_C_DEACTIVATED, UserExt.USER_STATUS_C_VALID);
        if (rows.isEmpty()) return false;
        final JSONObject article = rows.getFirst();
        final JSONObject viewer = new JSONObject().put(Keys.OBJECT_ID, article.getString("viewerId"))
                .put(User.USER_NAME, article.optString("viewerName")).put(User.USER_ROLE, article.optString("viewerRole"));
        final Set<String> invited = Article.ARTICLE_TYPE_C_DISCUSSION == article.optInt(Article.ARTICLE_TYPE)
                ? UserMentionCandidates.parse(article.optString(Article.ARTICLE_CONTENT)) : Set.of();
        return ApiV3ContentUtil.canViewArticle(article, viewer, invited);
    }
}
