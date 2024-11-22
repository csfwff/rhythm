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

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.json.JSONObject;

@Repository
public class UploadRepository extends AbstractRepository {
    /**
     * Public constructor.
     */
    public UploadRepository() {
        super("upload");
    }

    public void add(String type, String userName, String ip, String time, String path, String md5, boolean isPublic) throws RepositoryException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        jsonObject.put("userName", userName);
        jsonObject.put("ip", ip);
        jsonObject.put("time", time);
        jsonObject.put("path", path);
        jsonObject.put("md5", md5);
        jsonObject.put("public", isPublic);
        add(jsonObject);
    }

}
