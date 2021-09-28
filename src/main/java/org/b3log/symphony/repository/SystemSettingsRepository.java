package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.FilterOperator;
import org.b3log.latke.repository.PropertyFilter;
import org.b3log.latke.repository.Query;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.b3log.symphony.model.SystemSettings;
import org.json.JSONObject;


@Repository
public class SystemSettingsRepository extends AbstractRepository {
    /**
     * Public constructor.
     */
    public SystemSettingsRepository() {
        super(SystemSettings.SYSTEM_SETTINGS);
    }

    @Override
    public String add(final JSONObject jsonObject) throws RepositoryException {
        return super.add(jsonObject);
    }

    public JSONObject getByUsrId(final String userId) throws RepositoryException {
        final Query query = new Query();
        query.setFilter(new PropertyFilter("userId", FilterOperator.EQUAL, userId));
        return getFirst(query);
    }

}
