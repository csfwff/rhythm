package org.b3log.symphony.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.regex.Pattern;

/** Stateless helpers; persisted credentials are always SHA-256 hashes. */
public final class ApiV3TokenUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Pattern TOKEN = Pattern.compile("[A-Za-z0-9_-]{43,256}");

    private ApiV3TokenUtil() {
    }

    public static String generate() {
        final byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static boolean isValidFormat(final String token) {
        return null != token && TOKEN.matcher(token).matches();
    }

    /** Cookie Max-Age and token response durations both use seconds. */
    public static int cookieMaxAgeSeconds(final long seconds) {
        return (int) Math.max(0L, Math.min(Integer.MAX_VALUE, seconds));
    }

    public static String hash(final String token) throws Exception {
        final byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(("rhythm-api-v3:" + token).getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
    }
}
