package org.b3log.symphony.repository;

import org.b3log.latke.repository.AbstractRepository;
import org.b3log.latke.repository.RepositoryException;
import org.b3log.latke.repository.annotation.Repository;
import org.json.JSONObject;

@Repository
public class ChatRoomRepository extends AbstractRepository {

    /**
     * Public constructor.
     */
    public ChatRoomRepository() { super("chat_room"); }

    @Override
    public String add(JSONObject jsonObject) throws RepositoryException {
        return super.add(jsonObject);
    }
}
