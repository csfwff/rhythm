package org.b3log.symphony.util;

import org.b3log.symphony.model.UserExt;

/** Existing registration/rename contract, shared without session or request dependencies. */
public final class UserNameRules {
    public static final int MIN_LENGTH = 1;
    public static final int MAX_LENGTH = 64;
    private UserNameRules() { }
    public static boolean invalid(final String name) {
        if (null == name || name.length() < MIN_LENGTH || name.length() > MAX_LENGTH
                || "FileTransfer".equals(name) || UserExt.isReservedUserName(name)) return true;
        for (int i = 0; i < name.length(); i++) {
            final char c = name.charAt(i);
            if (!(c >= 'a' && c <= 'z') && !(c >= 'A' && c <= 'Z') && !(c >= '0' && c <= '9') && c != '-') return true;
        }
        return false;
    }
}
