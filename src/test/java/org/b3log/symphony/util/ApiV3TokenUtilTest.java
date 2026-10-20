package org.b3log.symphony.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ApiV3TokenUtilTest {
    @Test
    public void tokensHaveFullEntropyAndNoDuplicatesInSample() {
        final Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            final String token = ApiV3TokenUtil.generate();
            Assert.assertEquals(token.length(), 43);
            Assert.assertTrue(ApiV3TokenUtil.isValidFormat(token));
            Assert.assertEquals(Base64.getUrlDecoder().decode(token).length, 32);
            Assert.assertTrue(tokens.add(token));
        }
    }

    @Test
    public void malformedTokensCannotReachStorageLookup() {
        for (final String token : new String[]{"", "short", "a".repeat(257), "a".repeat(42) + "=", "a".repeat(43) + "\n", "a".repeat(43) + "?"}) {
            Assert.assertFalse(ApiV3TokenUtil.isValidFormat(token));
        }
        Assert.assertFalse(ApiV3TokenUtil.isValidFormat(null));
    }

    @Test
    public void hashesAreStableDistinctAndDomainSeparated() throws Exception {
        final String token = ApiV3TokenUtil.generate();
        final String hash = ApiV3TokenUtil.hash(token);
        Assert.assertEquals(hash, ApiV3TokenUtil.hash(token));
        Assert.assertNotEquals(hash, token);
        Assert.assertNotEquals(hash, ApiV3TokenUtil.hash(ApiV3TokenUtil.generate()));
        final String plain = Base64.getUrlEncoder().withoutPadding().encodeToString(
                MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8)));
        Assert.assertNotEquals(hash, plain);
    }

    @Test
    public void refreshCookiePreservesThirtyDayDuration() {
        final long seconds = TimeUnit.DAYS.toSeconds(30);
        Assert.assertEquals(ApiV3TokenUtil.cookieMaxAgeSeconds(seconds), 2592000);
    }

    @Test
    public void cookieDurationCannotOverflowOrBecomeSessionCookie() {
        Assert.assertEquals(ApiV3TokenUtil.cookieMaxAgeSeconds(Long.MAX_VALUE), Integer.MAX_VALUE);
        Assert.assertEquals(ApiV3TokenUtil.cookieMaxAgeSeconds(0), 0);
        Assert.assertEquals(ApiV3TokenUtil.cookieMaxAgeSeconds(-1), 0);
    }

    /** Also runnable in isolation without starting the server or a Maven build. */
    public static void main(final String[] args) throws Exception {
        final ApiV3TokenUtilTest test = new ApiV3TokenUtilTest();
        test.tokensHaveFullEntropyAndNoDuplicatesInSample();
        test.malformedTokensCannotReachStorageLookup();
        test.hashesAreStableDistinctAndDomainSeparated();
        test.refreshCookiePreservesThirtyDayDuration();
        test.cookieDurationCannotOverflowOrBecomeSessionCookie();
        System.out.println("PASS: 5 ApiV3TokenUtil tests");
    }
}
