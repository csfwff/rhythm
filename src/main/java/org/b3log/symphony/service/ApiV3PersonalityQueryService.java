package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.ApiV3Personality;
import org.b3log.symphony.util.ApiV3Requests;
import org.b3log.symphony.util.ApiV3WriteInputs;
import org.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import java.util.List;

/** Current-owner MBTI only; does not read the aggregate profile or use page-session identity. */
@Service
public class ApiV3PersonalityQueryService {
    @Inject private UserRepository users;
    public JSONObject read(final String actor) throws RepositoryException {
        if (actor == null || !actor.matches("[1-9][0-9]{0,18}")) throw ApiV3WriteInputs.invalid();
        final List<JSONObject> rows;
        try {
            rows = users.select("SELECT mbti FROM `" + users.getName() + "` WHERE oId=? AND userStatus=?", actor, UserExt.USER_STATUS_C_VALID);
        } catch (final RepositoryException e) {
            LogManager.getLogger(getClass()).warn("Personality settings read unavailable");
            throw new ApiV3Exception(503, "temporarily_unavailable", "MBTI 设置暂不可用");
        }
        if (rows.size() != 1) throw ApiV3Requests.notFound();
        return ApiV3Personality.dto(rows.getFirst());
    }
}
