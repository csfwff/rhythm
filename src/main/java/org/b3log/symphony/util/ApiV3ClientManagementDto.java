package org.b3log.symphony.util;

import org.b3log.symphony.service.ApiV3TokenMgmtService;
import org.json.JSONObject;
import java.util.Arrays;
import java.util.Set;

/** Even stored registrations pass the bounded public-field contract before reaching an administrator. */
public final class ApiV3ClientManagementDto {
    private ApiV3ClientManagementDto() { }
    public static JSONObject client(final JSONObject row) {
        final String id = string(row,"clientId",64), name = string(row,"name",128), scope = string(row,"scope",255);
        if (!id.matches("[A-Za-z0-9_-]{1,64}") || name.isBlank()) throw unavailable();
        final Set<String> available = Set.copyOf(Arrays.asList(ApiV3TokenMgmtService.DEFAULT_SCOPES.split(" ")));
        for (final String item : scope.split(" ",-1)) if (!available.contains(item)) throw unavailable();
        return new JSONObject().put("clientId",id).put("name",name).put("scope",scope).put("status",status(row));
    }
    public static JSONObject redirect(final JSONObject row) {
        final String uri = string(row,"redirectUri",2048);
        // Reading a registered development loopback callback does not approve its use in production.
        if (!ApiV3OAuthRedirects.valid(uri,true)) throw unavailable();
        return new JSONObject().put("uri",uri).put("status",status(row));
    }
    private static String string(final JSONObject row,final String key,final int max) {
        if (!(row.opt(key) instanceof String value) || value.isEmpty() || value.length()>max
                || value.codePoints().anyMatch(Character::isISOControl)) throw unavailable();
        try { ApiV3Json.canonical(value); }
        catch(IllegalArgumentException e) { throw unavailable(); }
        return value;
    }
    private static int status(final JSONObject row) {
        if (!(row.opt("status") instanceof Number value) || !("0".equals(value.toString())||"1".equals(value.toString()))) throw unavailable();
        return value.intValue();
    }
    private static ApiV3Exception unavailable() { return new ApiV3Exception(503,"temporarily_unavailable","客户端管理暂不可用"); }
}
