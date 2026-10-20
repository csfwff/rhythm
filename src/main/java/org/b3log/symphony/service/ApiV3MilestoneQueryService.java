package org.b3log.symphony.service;

import org.b3log.latke.ioc.Inject;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.repository.MilestoneRepository;
import org.b3log.symphony.util.ApiV3MilestoneDto;
import org.b3log.symphony.util.ApiV3Requests;
import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ApiV3MilestoneQueryService {
    @Inject private MilestoneRepository milestones;
    public JSONObject list(final String actor, final int status, final int page, final int size) throws RepositoryException {
        final JSONArray list = new JSONArray();
        for (final JSONObject row : milestones.pageOwned(actor, status, page, size)) list.put(ApiV3MilestoneDto.owned(row));
        return ApiV3Requests.pagination(list, page, size, milestones.countOwned(actor, status));
    }
    public JSONObject detail(final String actor, final String id) throws RepositoryException {
        final JSONObject row = milestones.getOwned(actor, id);
        if (null == row) throw ApiV3Requests.notFound();
        return ApiV3MilestoneDto.owned(row);
    }
}
