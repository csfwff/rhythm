package org.b3log.symphony.service;

import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.ChatMessageValidation;
import org.b3log.symphony.util.ReservedWords;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

/** Callers explicitly supply the authenticated actor; no Sessions/apiKey lookup or database commit. */
@Service
public class ChatMessageValidationService {
    protected long now(){return System.currentTimeMillis();}
    public String prepare(JSONObject actor,String content){
        return ChatMessageValidation.prepare(actor,content,now(),Symphonys.MIN_STEP_CHAT_TIME,ReservedWords::processReservedWord);
    }
}
