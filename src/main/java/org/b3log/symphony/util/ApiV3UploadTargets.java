package org.b3log.symphony.util;

import java.net.URI;
import java.util.Set;

/** Only the configured public upload base is returned; internal URLs are never used. */
public final class ApiV3UploadTargets {
    private ApiV3UploadTargets() { }
    public static String endpoint(final String base) {
        if (null == base || base.isBlank() || base.length() > 2048 || !base.equals(base.trim())
                || base.codePoints().anyMatch(Character::isISOControl)) throw new IllegalArgumentException("upload target");
        final URI uri;
        try { uri = new URI(base); } catch (final Exception e) { throw new IllegalArgumentException("upload target"); }
        if (null == uri.getHost() || null != uri.getRawUserInfo() || null != uri.getRawQuery() || null != uri.getRawFragment()
                || uri.getPort() == 0 || uri.getPort() > 65535 || uri.getPort() < -1) throw new IllegalArgumentException("upload target");
        final String host = uri.getHost().toLowerCase(java.util.Locale.ROOT);
        if (!"https".equalsIgnoreCase(uri.getScheme()) && !("http".equalsIgnoreCase(uri.getScheme())
                && Set.of("localhost", "127.0.0.1", "[::1]").contains(host))) throw new IllegalArgumentException("upload target");
        if (!uri.normalize().getRawPath().equals(uri.getRawPath()) || uri.getRawPath().contains("%")
                || uri.getRawPath().contains("//")) throw new IllegalArgumentException("upload target");
        return base.replaceAll("/+$", "") + "/api/v1/files";
    }
}
