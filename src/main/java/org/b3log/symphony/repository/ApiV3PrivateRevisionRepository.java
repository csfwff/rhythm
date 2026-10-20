package org.b3log.symphony.repository;

import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3PrivateRevision;

@Repository
public class ApiV3PrivateRevisionRepository extends ApiV3RevisionRepository {
    public ApiV3PrivateRevisionRepository(){super(ApiV3PrivateRevision.REPOSITORY);}
}
