package org.b3log.symphony.util;

import org.apache.commons.lang.StringUtils;
import org.b3log.latke.util.CollectionUtils;
import java.util.*;

/** Legacy mention delimiters, without cached user lookups. Caller must validate current users. */
public final class UserMentionCandidates {
    private UserMentionCandidates() { }
    public static Set<String> parse(final String text) {
        final Set<String> ret = new HashSet<>();
        int idx = text.indexOf('@');
        if (-1 == idx) {
            return ret;
        }

        String copy = text.trim();
        copy = copy.replaceAll("\\n", " ");
        String[] uNames = StringUtils.substringsBetween(copy, "@", " ");
        String tail = StringUtils.substringAfterLast(copy, "@");
        if (tail.contains(" ")) {
            tail = null;
        }

        if (null != tail) {
            if (null == uNames) {
                uNames = new String[1];
                uNames[0] = tail;
            } else {
                uNames = Arrays.copyOf(uNames, uNames.length + 1);
                uNames[uNames.length - 1] = tail;
            }
        }

        String[] uNames2 = StringUtils.substringsBetween(copy, "@", "<");
        final Set<String> maybeUserNameSet;
        if (null == uNames) {
            uNames = uNames2;

            if (null == uNames) {
                return ret;
            }

            maybeUserNameSet = CollectionUtils.arrayToSet(uNames);
        } else {
            maybeUserNameSet = CollectionUtils.arrayToSet(uNames);

            if (null != uNames2) {
                maybeUserNameSet.addAll(CollectionUtils.arrayToSet(uNames2));
            }
        }

        for (String candidate : maybeUserNameSet) ret.add(candidate.trim());
        return ret;
    }
}
