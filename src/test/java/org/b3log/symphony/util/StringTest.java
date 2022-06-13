package org.b3log.symphony.util;

import org.testng.Assert;
import org.testng.annotations.Test;

public class StringTest {
    @Test
    public void test() {
        final String fromId = "1632113656469";
        final String toId = "1632113664493";
        String chatHex = Strings.uniqueId(new String[]{fromId, toId});
        String chatHexReverse = Strings.uniqueId(new String[]{toId, fromId});
        Assert.assertEquals(chatHex, chatHexReverse);
    }
}
