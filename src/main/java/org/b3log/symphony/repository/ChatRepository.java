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

import java.util.ArrayList;
import java.util.List;

@Repository
public class ChatRepository extends AbstractRepository {
    /**
     * Public constructor.
     */
    public ChatRepository() {
        super("chat");
    }

    public List<JSONObject> getMessagesBySenderId(final String senderId) throws RepositoryException {
        final Query query = new Query().setFilter(
                new PropertyFilter("senderId", FilterOperator.EQUAL, senderId)
        );
        return getJsonObjects(query);
    }

    public JSONObject getMessageById(final String id) throws RepositoryException {
        final Query query = new Query().setFilter(
                new PropertyFilter("oId", FilterOperator.EQUAL, id)
        );
        JSONObject result = getFirst(query);
        return new JSONObject(result.optString("message")).put("mapId", result.optString("oId"));
    }

    public List<JSONObject> getMessagesByReceiverId(final String receiverId) throws RepositoryException {
        final Query query = new Query().setFilter(
                new PropertyFilter("receiverId", FilterOperator.EQUAL, receiverId)
        );
        return getJsonObjects(query);
    }

    private List<JSONObject> getJsonObjects(Query query) throws RepositoryException {
        List<JSONObject> result = getList(query);
        List<JSONObject> message = new ArrayList<>();
        for (JSONObject r : result) {
            message.add(new JSONObject(r.optString("message")).put("mapId", r.optString("oId")));
        }
        return message;
    }
}
