package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.repository.MembershipRepository;
import org.b3log.symphony.repository.MembershipLevelRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.util.ApiV3MembershipDto;
import org.b3log.symphony.util.ApiV3Requests;
import org.json.JSONArray;
import org.json.JSONObject;

/** Uncached owner status and paginated public plan metadata; no billing or configuration mutation. */
@Service
public class ApiV3MembershipQueryService {
    @Inject private UserRepository users;
    @Inject private MembershipRepository memberships;
    @Inject private MembershipLevelRepository levels;

    public JSONObject me(final JSONObject viewer) {
        final String actor = viewer == null ? "" : viewer.optString("oId");
        if (!actor.matches("[1-9][0-9]{0,18}")) throw ApiV3Requests.forbidden();
        try {
            final var current = users.select("SELECT oId FROM `" + users.getName() + "` WHERE oId=? AND userStatus=? LIMIT 1",
                    actor, UserExt.USER_STATUS_C_VALID);
            if (current.size() != 1) throw ApiV3Requests.notFound();
            final var rows = memberships.select("SELECT lvCode,expiresAt,configJson FROM `" + memberships.getName()
                    + "` WHERE userId=? AND state=? LIMIT 2", actor, 1);
            if (rows.size() > 1) throw ApiV3MembershipDto.unavailable();
            return ApiV3MembershipDto.membership(rows.isEmpty() ? null : rows.getFirst(), System.currentTimeMillis());
        } catch (RepositoryException e) { throw ApiV3MembershipDto.unavailable(); }
    }

    public JSONObject levels(final int page, final int size) {
        if (page < 1 || page > Integer.MAX_VALUE / 50 || size < 1 || size > 50) throw ApiV3Requests.invalid();
        try {
            final var counts = levels.select("SELECT COUNT(*) AS total FROM `" + levels.getName() + "`");
            if (counts.size() != 1) throw ApiV3MembershipDto.unavailable();
            final long total = counts.getFirst().getLong("total");
            final var rows = levels.select("SELECT oId,lvCode,lvName,price,durationType,durationValue,benefits FROM `"
                    + levels.getName() + "` ORDER BY price ASC,oId ASC LIMIT ? OFFSET ?", size, (page - 1) * size);
            final var list = new JSONArray();
            for (final var row : rows) list.put(ApiV3MembershipDto.level(row));
            return ApiV3Requests.pagination(list, page, size, total);
        } catch (RepositoryException e) { throw ApiV3MembershipDto.unavailable(); }
    }
}
