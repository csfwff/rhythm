package org.b3log.symphony.service;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.repository.ChatRoomRepository;
import org.json.JSONObject;

@Service
public class ChatRoomService {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(ChatRoomService.class);

    @Inject
    private ChatRoomRepository chatRoomRepository;

    /**
     * Get chat msg by specified oId.
     * @param oId the specified oId
     * @return chat msg
     */
    public JSONObject getChatMsg(final String oId){
        try {
           return chatRoomRepository.get(oId);
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Get chat msg failed", e);
            return null;
        }
    }
}
