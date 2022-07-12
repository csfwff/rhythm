/*
 * Rhythm - A modern community (forum/BBS/SNS/blog) platform written in Java.
 * Modified version from Symphony, Thanks Symphony :)
 * Copyright (C) 2012-present, b3log.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
