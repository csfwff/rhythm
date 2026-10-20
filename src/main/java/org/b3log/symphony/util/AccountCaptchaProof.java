package org.b3log.symphony.util;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import org.json.JSONObject;

/** Bounded opaque proof from the existing captcha widget; no user-supplied validation URL. */
public final class AccountCaptchaProof {
    private AccountCaptchaProof(){ }
    public static String validate(String proof){
        if(proof==null||proof.isEmpty()||proof.getBytes(StandardCharsets.UTF_8).length>4096)throw ApiV3WriteInputs.invalid();
        final JSONObject body;
        try{body=ApiV3Json.parse(proof.getBytes(StandardCharsets.UTF_8));}catch(IllegalArgumentException e){throw ApiV3WriteInputs.invalid();}
        if(!body.keySet().equals(Set.of("lot_number","captcha_output","pass_token","gen_time")))throw ApiV3WriteInputs.invalid();
        for(String key:body.keySet()){
            if(!(body.opt(key) instanceof String value)||value.isEmpty()||value.length()>2048
                    ||value.codePoints().anyMatch(c->Character.isISOControl(c)))throw ApiV3WriteInputs.invalid();
        }
        return body.toString();
    }
}
