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
