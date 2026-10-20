package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.processor.channel.ArticleChannel;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import org.json.JSONObject;

/** Approximate current-node article connection count, with current database authorization first. */
@Service
public class ApiV3ArticleHeatQueryService {
    @Inject private ApiV3RealtimeTargetService targets;
    @Inject private ApiV3RealtimeService realtime;
    public JSONObject read(final String actor, final String articleId) throws RepositoryException {
        if (actor == null || !actor.matches("[0-9]{1,32}") || articleId == null || !articleId.matches("[0-9]{1,32}")) throw ApiV3WriteInputs.invalid();
        try {
            if (!targets.articleVisible(actor, articleId)) throw ApiV3Requests.notFound();
        } catch (final RepositoryException e) {
            throw new ApiV3Exception(503, "temporarily_unavailable", "文章热度暂不可用");
        }
        final Integer legacy = ArticleChannel.ARTICLE_VIEWS.get(articleId);
        final long count = (legacy == null ? 0L : Math.max(0L, legacy.longValue())) + realtime.articleConnectionCount(articleId);
        return new JSONObject().put("articleId", articleId).put("heat", count);
    }
}
