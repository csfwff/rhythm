package org.b3log.symphony.repository;

import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3NotificationRevision;

@Repository
public class ApiV3NotificationRevisionRepository extends ApiV3RevisionRepository {
    public ApiV3NotificationRevisionRepository(){super(ApiV3NotificationRevision.REPOSITORY);}
}
