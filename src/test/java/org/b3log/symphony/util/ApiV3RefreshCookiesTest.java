package org.b3log.symphony.util;

import org.testng.Assert;

public class ApiV3RefreshCookiesTest {
    public static void main(final String[] args) {
        for (final long seconds : new long[]{0, 60, Long.MAX_VALUE}) {
            final var cookie = ApiV3RefreshCookies.create(seconds == 0 ? "" : "opaque", seconds);
            Assert.assertEquals(cookie.getName(), "fp_v3_refresh");
            Assert.assertEquals(cookie.getValue(), seconds == 0 ? "" : "opaque");
            Assert.assertEquals(cookie.getPath(), "/api/v3/auth");
            Assert.assertTrue(cookie.isHttpOnly()); Assert.assertTrue(cookie.isSecure());
            Assert.assertEquals(cookie.getSameSite(), "Strict"); Assert.assertNull(cookie.getDomain());
            Assert.assertEquals(cookie.getMaxAge(), (long) ApiV3TokenUtil.cookieMaxAgeSeconds(seconds));
        }
        System.out.println("PASS actual Latke refresh-cookie API, issue/clear flags, host-only domain and bounded max age");
    }
}
