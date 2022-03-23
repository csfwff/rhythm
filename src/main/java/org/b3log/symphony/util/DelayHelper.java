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

import java.text.SimpleDateFormat;
import java.util.Date;

public class DelayHelper {

    private static int times = 0;
    private static long previousTime = 0L;

    public static void step() {
        times++;
        final long timeNow = System.currentTimeMillis();
        final String timeNowStr = new SimpleDateFormat("mm:ss.SS").format(new Date(timeNow));
        long timeInterval = timeNow - previousTime;
        if (timeInterval > 5000 && times != 1) {
            clear();
            timeInterval = timeNow - previousTime;
            times++;
        }
        if (times == 1) {
            timeInterval = 0;
        }
        final float timeIntervalSecond = timeInterval / 1000;
        System.out.println(times + " - " + timeNowStr + " - " + timeIntervalSecond + "s" + " - " + timeInterval + "ms");
        previousTime = timeNow;
    }

    public static void clear() {
        previousTime = 0L;
        times = 0;
    }

    public static void start() {
        clear();
        step();
    }

    public static void end() {
        step();
        clear();
    }
}
