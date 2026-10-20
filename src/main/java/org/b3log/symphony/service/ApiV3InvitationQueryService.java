package org.b3log.symphony.service;

import org.apache.logging.log4j.LogManager;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.InvitecodeRepository;
import org.b3log.symphony.repository.UserRepository;
import org.b3log.symphony.util.ApiV3Invitation;
import org.b3log.symphony.util.ApiV3Requests;
import org.json.JSONObject;

/** Read the stored state of one supplied code. Never list codes or access reservation/owner fields. */
@Service
public class ApiV3InvitationQueryService {
    @Inject private UserRepository users;
    @Inject private InvitecodeRepository invitations;
    private void requireActor(final String actor) throws RepositoryException {
        if (actor == null || !actor.matches("[1-9][0-9]{0,18}")) throw ApiV3Requests.invalid();
        if (users.select("SELECT oId FROM `" + users.getName() + "` WHERE oId=? AND userStatus=?", actor,
                UserExt.USER_STATUS_C_VALID).size() != 1) throw ApiV3Requests.notFound();
    }
    public JSONObject status(final String actor, final String supplied) {
        final String code = ApiV3Invitation.code(supplied);
        try {
            requireActor(actor);
            final var rows = invitations.select("SELECT status FROM `" + invitations.getName() + "` WHERE code=? LIMIT 2", code);
            if (rows.isEmpty()) throw ApiV3Requests.notFound();
            if (rows.size() != 1) throw ApiV3Invitation.unavailable();
            final JSONObject result = ApiV3Invitation.status(rows.getFirst());
            requireActor(actor);
            return result;
        } catch (final RepositoryException e) {
            LogManager.getLogger(getClass()).warn("Invitation status read unavailable");
            throw ApiV3Invitation.unavailable();
        }
    }
}
