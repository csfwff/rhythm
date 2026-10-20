package org.b3log.symphony.repository;

import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.ApiV3ArticleRevision;

@Repository
public class ApiV3ArticleRevisionRepository extends ApiV3RevisionRepository {
    public ApiV3ArticleRevisionRepository(){super(ApiV3ArticleRevision.REPOSITORY);}
}
