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
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Comment;
import org.b3log.symphony.model.Reward;
import org.b3log.symphony.model.Tag;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.ArticleRepository;
import org.b3log.symphony.repository.CommentRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.ApiV3ProfileQueries;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3ContentUtil;
import org.b3log.symphony.util.Emotions;
import org.b3log.symphony.util.Markdowns;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/** V3 content read models, independent of session-dependent page enrichment. */
@Service
public class ApiV3ContentQueryService {
    @Inject
    private UserRepository userRepository;
    @Inject
    private ArticleRepository articleRepository;
    @Inject
    private CommentRepository commentRepository;
    @Inject
    private UserQueryService userQueryService;
    @Inject
    private ApiV3ContentDtoService contentDtoService;
    @Inject
    private RewardQueryService rewardQueryService;
    @Inject
    private ApiV3DiscoveryQueryService discoveryQueryService;

    public JSONObject articles(final JSONObject viewer, final int p, final int size, final String sort,
                               final String type) throws RepositoryException {
        return discoveryQueryService.articles(viewer, p, size, sort, type, ApiV3DiscoveryQueryService.Selection.all());
    }

    /** Returns null for both missing and invisible resources, before serializing any metadata. */
    public JSONObject viewableArticle(final String id, final JSONObject viewer) throws RepositoryException {
        final JSONObject article = articleRepository.get(id);
        if (null == article) return null;
        final Set<String> invited = null != viewer && Article.ARTICLE_TYPE_C_DISCUSSION == article.optInt(Article.ARTICLE_TYPE)
                ? userQueryService.getUserNames(article.optString(Article.ARTICLE_CONTENT)) : Collections.emptySet();
        if (!ApiV3ContentUtil.canViewArticle(article, viewer, invited)) return null;
        final JSONObject author = userQueryService.getUser(article.optString(Article.ARTICLE_AUTHOR_ID));
        return isBlocked(author) ? null : article;
    }

    public java.util.function.Function<String, LongArticleParagraphService.TitleSnapshot> paragraphTitleSnapshot() {
        return id -> {
            try {
                final JSONObject article = articleRepository.get(id);
                if (null == article) return new LongArticleParagraphService.TitleSnapshot(null, false);
                final boolean visible = ApiV3ContentUtil.canViewArticle(article, null, Collections.emptySet())
                        && !isBlocked(userQueryService.getUser(article.optString(Article.ARTICLE_AUTHOR_ID)));
                return new LongArticleParagraphService.TitleSnapshot(article.optString(Article.ARTICLE_TITLE), visible);
            } catch (final RepositoryException e) { throw new IllegalStateException("Paragraph title snapshot failed", e); }
        };
    }

    public JSONObject detail(final JSONObject article, final JSONObject viewer) {
        final JSONObject dto = contentDtoService.summary(article);
        final boolean thought = Article.ARTICLE_TYPE_C_THOUGHT == article.optInt(Article.ARTICLE_TYPE);
        final String content = article.optString(Article.ARTICLE_CONTENT);
        dto.put("content", content).put("contentFormat", thought ? "thought" : "markdown")
                .put("contentHtml", thought ? "" : render(content));
        final String viewerId = null == viewer ? "" : viewer.optString(Keys.OBJECT_ID);
        final boolean rewarded = !viewerId.isEmpty() && (viewerId.equals(article.optString(Article.ARTICLE_AUTHOR_ID))
                || rewardQueryService.isRewarded(viewerId, article.optString(Keys.OBJECT_ID), Reward.TYPE_C_ARTICLE));
        final int rewardPoint = article.optInt(Article.ARTICLE_REWARD_POINT);
        dto.put("rewardPoint", rewardPoint).put("rewarded", rewarded);
        dto.put("rewardContent", rewardPoint > 0 && rewarded ? article.optString(Article.ARTICLE_REWARD_CONTENT) : "");
        dto.put("rewardContentHtml", rewardPoint > 0 && rewarded ? render(article.optString(Article.ARTICLE_REWARD_CONTENT)) : "");
        return dto;
    }

    public JSONObject comments(final JSONObject article, final JSONObject viewer, final int p, final int size,
                               final String sort) throws RepositoryException {
        final Filter filter = CompositeFilterOperator.and(
                new PropertyFilter(Comment.COMMENT_ON_ARTICLE_ID, FilterOperator.EQUAL, article.optString(Keys.OBJECT_ID)),
                new PropertyFilter(Comment.COMMENT_TYPE, FilterOperator.EQUAL, Comment.COMMENT_TYPE_C_ARTICLE),
                new PropertyFilter(Comment.COMMENT_STATUS, FilterOperator.EQUAL, Comment.COMMENT_STATUS_C_VALID));
        final Query query = new Query().setFilter(filter).setPageCount(1).setPage(p, size);
        if ("hot".equals(sort)) query.addSort(Comment.COMMENT_SCORE, SortDirection.DESCENDING);
        query.addSort(Keys.OBJECT_ID, "oldest".equals(sort) ? SortDirection.ASCENDING : SortDirection.DESCENDING);
        final List<JSONObject> rows = (List<JSONObject>) commentRepository.get(query).opt(Keys.RESULTS);
        final JSONArray list = new JSONArray();
        for (final JSONObject comment : rows) list.put(commentDto(comment, article, viewer));
        return paginated(list, p, size, commentRepository.count(new Query().setFilter(filter)));
    }

    /** Public, non-anonymous comments on discoverable articles; all visibility filters precede pagination. */
    public JSONObject userComments(final String owner, final JSONObject viewer, final int p, final int size) throws RepositoryException {
        final ApiV3ProfileQueries.Fragment fragment = ApiV3ProfileQueries.comments(commentRepository.getName(), articleRepository.getName(),
                userRepository.getName(), owner, null == viewer, UserExt.USER_STATUS_C_INVALID, UserExt.USER_STATUS_C_DEACTIVATED, Tag.TAG_TITLE_C_SANDBOX);
        final long total = commentRepository.select("SELECT COUNT(1) AS total" + fragment.sql(), fragment.args().toArray()).get(0).optLong("total");
        final List<Object> args = new ArrayList<>(fragment.args()); args.add((p - 1) * size); args.add(size);
        final List<JSONObject> comments = commentRepository.select("SELECT c.*" + fragment.sql()
                + " ORDER BY c.commentCreateTime DESC,c.oId DESC LIMIT ?,?", args.toArray());
        final JSONArray list = new JSONArray();
        for (final JSONObject comment : comments) {
            final JSONObject article = viewableArticle(comment.optString(Comment.COMMENT_ON_ARTICLE_ID), viewer);
            if (null == article) continue;
            list.put(commentDto(comment, article, viewer).put("type", comment.optInt(Comment.COMMENT_TYPE))
                    .put("article", contentDtoService.summary(article)));
        }
        return ApiV3Requests.pagination(list, p, size, total);
    }

    public JSONObject commentDto(final JSONObject comment, final JSONObject article, final JSONObject viewer) {
        final JSONObject author = userQueryService.getUser(comment.optString(Comment.COMMENT_AUTHOR_ID));
        final boolean visible = Comment.COMMENT_STATUS_C_VALID == comment.optInt(Comment.COMMENT_STATUS) && !isBlocked(author) && ApiV3ContentUtil.canViewCommentContent(comment, article, viewer);
        final String content = visible ? comment.optString(Comment.COMMENT_CONTENT) : "";
        return new JSONObject().put("id", comment.optString(Keys.OBJECT_ID))
                .put("articleId", article.optString(Keys.OBJECT_ID))
                .put("parentId", comment.optString(Comment.COMMENT_ORIGINAL_COMMENT_ID))
                .put("content", content).put("contentHtml", visible ? render(content) : "")
                .put("contentVisible", visible).put("createdAt", comment.optLong(Comment.COMMENT_CREATE_TIME))
                .put("anonymous", Comment.COMMENT_ANONYMOUS_C_ANONYMOUS == comment.optInt(Comment.COMMENT_ANONYMOUS))
                .put("author", contentDtoService.author(author, Comment.COMMENT_ANONYMOUS_C_ANONYMOUS == comment.optInt(Comment.COMMENT_ANONYMOUS)));
    }

    private static boolean isBlocked(final JSONObject user) {
        return null == user || UserExt.USER_STATUS_C_INVALID == user.optInt(UserExt.USER_STATUS)
                || UserExt.USER_STATUS_C_DEACTIVATED == user.optInt(UserExt.USER_STATUS);
    }

    private static String render(final String content) {
        return Markdowns.clean(Markdowns.toHTML(Emotions.convert(content)), "");
    }

    private static JSONObject paginated(final JSONArray list, final int p, final int size, final long total) {
        return new JSONObject().put("list", list).put("pagination", new JSONObject().put("p", p).put("size", size)
                .put("total", total).put("pageCount", (total + size - 1) / size));
    }
}
