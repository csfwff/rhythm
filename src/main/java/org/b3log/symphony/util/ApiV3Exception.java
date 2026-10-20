package org.b3log.symphony.util;

/** Expected v3 protocol or resource error; safe for the JSON response boundary. */
public class ApiV3Exception extends RuntimeException {
    public final int status;
    public final String code;
    public ApiV3Exception(final int status, final String code, final String message) {
        super(message);
        this.status = status;
        this.code = code;
    }
}
