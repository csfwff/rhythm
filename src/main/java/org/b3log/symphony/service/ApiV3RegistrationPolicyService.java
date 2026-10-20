package org.b3log.symphony.service;

import org.b3log.latke.Keys;
import org.b3log.latke.ioc.Inject;
import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.model.Option;
import org.b3log.symphony.model.Permission;
import org.b3log.symphony.model.UserExt;
import org.b3log.symphony.repository.OptionRepository;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.UserNameRules;
import org.json.JSONArray;
import org.json.JSONObject;

/** Public registration form policy, not proof that a later registration is authorized. */
@Service
public class ApiV3RegistrationPolicyService {
    @Inject private OptionRepository options;
    @Inject private UserQueryService users;
    @Inject private RoleQueryService roles;

    public JSONObject options(String referral)throws Exception {
        final String name=referral==null?"":referral.strip();
        if(!name.isEmpty()&&UserNameRules.invalid(name))throw new ApiV3Exception(422,"invalid_request","邀请人用户名不合法");
        final var settings=options.select("SELECT optionValue FROM `"+options.getName()+"` WHERE oId=?",Option.ID_C_MISC_ALLOW_REGISTER);
        final JSONObject setting=settings.size()==1?settings.getFirst():null;
        final String value=setting==null?"":setting.optString(Option.OPTION_VALUE);
        final String mode=switch(value){case "0"->"open";case "1"->"closed";case "2"->"invitation";default->throw new ApiV3Exception(503,"temporarily_unavailable","注册状态暂不可用");};
        boolean accepted=false;
        // Closing registration takes precedence over every invitation permission, as in the old write entry.
        if(!"closed".equals(mode)&&!name.isEmpty()){
            final JSONObject inviter=users.getUserByName(name);
            if(inviter!=null){
                final var permissions=roles.getUserPermissionsGrantMap(inviter.optString(Keys.OBJECT_ID));
                final var grant=permissions==null?null:permissions.get(Permission.PERMISSION_ID_C_COMMON_USE_INVITATION_LINK);
                accepted=grant!=null&&grant.optBoolean(Permission.PERMISSION_T_GRANT);
            }
        }
        return new JSONObject().put("mode",mode).put("enabled",!"closed".equals(mode))
                .put("inviteCodeRequired","invitation".equals(mode)&&!accepted).put("referralAccepted",accepted)
                .put("username",new JSONObject().put("minLength",UserNameRules.MIN_LENGTH).put("maxLength",UserNameRules.MAX_LENGTH))
                .put("appRoles",new JSONArray().put(new JSONObject().put("value",UserExt.USER_APP_ROLE_C_HACKER).put("label","程序员"))
                        .put(new JSONObject().put("value",UserExt.USER_APP_ROLE_C_PAINTER).put("label","设计师")));
    }
}
