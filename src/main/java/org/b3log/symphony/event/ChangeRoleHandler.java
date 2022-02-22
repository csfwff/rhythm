package org.b3log.symphony.event;

import org.apache.commons.lang3.StringUtils;
import org.b3log.latke.Keys;
import org.b3log.latke.event.AbstractEventListener;
import org.b3log.latke.event.Event;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.ioc.Singleton;
import org.b3log.latke.model.User;
import org.b3log.latke.service.ServiceException;
import org.b3log.symphony.model.Article;
import org.b3log.symphony.model.Common;
import org.b3log.symphony.model.Role;
import org.b3log.symphony.service.UserMgmtService;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

/**
 * 新人报道自动切换角色
 *
 * @author fangcong
 * @version 0.0.1
 * @since Created by work on 2022-02-22 13:28
 **/
@Singleton
public class ChangeRoleHandler extends AbstractEventListener<JSONObject> {


    @Inject
    private UserMgmtService userMgmtService;

    @Override
    public String getEventType() {
        return EventTypes.ADD_ARTICLE;
    }

    @Override
    public void action(Event<JSONObject> event) {
        final JSONObject data = event.getData();
        final String tags = data.optString(Article.ARTICLE_TAGS);
        final JSONObject author = data.optJSONObject("author");

        if (StringUtils.isBlank(tags)) {
            return;
        }
        final String[] tagArray = tags.split(",");
        final List<String> arrays = Arrays.asList(tagArray);
        if (tagArray.length == 0 || (!arrays.contains("新人报到") && !arrays.contains("新人报道"))) {
            return;
        }
        if (!author.optString(User.USER_ROLE).equals(Role.ROLE_ID_C_DEFAULT)) {
            return;
        } else {
            author.put(User.USER_ROLE, "1630553360689");
        }

        try {
            userMgmtService.updateUser(author.optString("oId"), author);
        } catch (ServiceException e) {
            e.printStackTrace();
        }
    }
}
