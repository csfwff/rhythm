package org.b3log.symphony.service;

import org.b3log.latke.service.annotation.Service;
import org.b3log.symphony.util.ApiV3Exception;
import org.b3log.symphony.util.Symphonys;
import org.json.JSONObject;

/** Public widget identifier only; never exposes its server verification key or configuration map. */
@Service
public class AccountCaptchaService {
    protected String configuredId(){return Symphonys.get("jiyan.id");}
    public JSONObject options(){
        final String value=configuredId();
        if(value==null||!value.matches("[A-Za-z0-9_-]{1,128}"))throw new ApiV3Exception(503,"temporarily_unavailable","验证组件暂不可用");
        return new JSONObject().put("provider","geetest4").put("captchaId",value);
    }
}
