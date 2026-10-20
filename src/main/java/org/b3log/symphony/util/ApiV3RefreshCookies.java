package org.b3log.symphony.util;

import org.b3log.latke.http.Cookie;

/** Cookie construction uses the project's actual Latke String-based SameSite API. */
public final class ApiV3RefreshCookies {
    public static final String NAME = "fp_v3_refresh";
    private ApiV3RefreshCookies() { }
    public static Cookie create(final String value, final long seconds) {
        final Cookie cookie = new Cookie(NAME, value);
        cookie.setPath("/api/v3/auth");
        cookie.setMaxAge(ApiV3TokenUtil.cookieMaxAgeSeconds(seconds));
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setSameSite("Strict");
        return cookie;
    }
}
