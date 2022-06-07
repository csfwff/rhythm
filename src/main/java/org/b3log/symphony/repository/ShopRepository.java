/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package org.b3log.symphony.repository;

import org.b3log.latke.repository.*;
import org.b3log.latke.repository.annotation.Repository;
import org.json.JSONObject;

import java.util.List;

@Repository
public class ShopRepository extends AbstractRepository {

    /**
     * Public constructor.
     */
    public ShopRepository() { super("shop"); }

    @Override
    public String add(JSONObject jsonObject) throws RepositoryException {
        return super.add(jsonObject);
    }

    public List<JSONObject> getByType(String type) throws RepositoryException {
        final Query query = new Query().setFilter(
                new PropertyFilter("type", FilterOperator.EQUAL, type));
        return getList(query);
    }
}
