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

import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.b3log.latke.http.RequestContext;
import org.b3log.latke.ioc.Singleton;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

@Singleton
public class Vocation {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Vocation.class);

    final public static String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36";
    final public static JSONObject vocationData = new JSONObject();

    public void vocation(final RequestContext context) {
        context.renderJSON(vocationData);
    }

    public static boolean refresh() {
        try {
            vocationData.put("type", -1);
            vocationData.put("dayName", "");
            vocationData.put("vName", "");
            vocationData.put("vRest", -1);
            vocationData.put("wRest", -1);
            final HttpResponse response = HttpRequest.get("https://timor.tech/api/holiday/info/" + today())
                    .connectionTimeout(30000).timeout(70000).header("User-Agent", UA)
                    .send();
            if (200 == response.statusCode()) {
                response.charset("UTF-8");
                final JSONObject result = new JSONObject(response.bodyText());
                // 节假日类型，分别表示 0工作日、1周末、2节日、3调休。
                int type = result.optJSONObject("type").optInt("type");
                vocationData.put("type", type);
                // 没有假期显示周几，有的话显示假期，比如周六、国庆节
                String dayName = result.optJSONObject("type").optString("name");
                vocationData.put("dayName", dayName);
                // 如果是周末或者节日，获取距离上班还有多久
                if (type == 1 || type == 2) {
                    final HttpResponse workday = HttpRequest.get("http://timor.tech/api/holiday/workday/next/" + today())
                            .connectionTimeout(30000).timeout(70000).header("User-Agent", UA)
                            .send();
                    if (200 == workday.statusCode()) {
                        workday.charset("UTF-8");
                        final JSONObject workdayResult = new JSONObject(workday.bodyText());
                        int wRest = workdayResult.optJSONObject("workday").optInt("rest");
                        vocationData.put("wRest", wRest);
                    }
                }
                // 如果是工作日或者调休，获取下一个节假日
                if (type == 0 || type == 3) {
                    final HttpResponse weekend = HttpRequest.get("http://timor.tech/api/holiday/next/" + today() + "?type=Y&week=Y")
                            .connectionTimeout(30000).timeout(70000).header("User-Agent", UA)
                            .send();
                    if (200 == weekend.statusCode()) {
                        weekend.charset("UTF-8");
                        final JSONObject weekendResult = new JSONObject(weekend.bodyText());
                        String vName = weekendResult.optJSONObject("holiday").optString("name"); // 下一个假期的名字
                        vocationData.put("vName", vName);
                        int vRest = weekendResult.optJSONObject("holiday").optInt("rest"); // 还有几天放假
                        vocationData.put("vRest", vRest);
                    }
                }
                System.out.println(">>> Vocation Date has refreshed. [vocationData=\"" + vocationData + "\"]");
                return true;
            } else {
                System.out.println(">>> Failed to refresh Vocation Date cause the connection is refused. [vocationData=\"" + vocationData + "\"]");
                return false;
            }
        } catch (Exception e) {
            LOGGER.log(Level.ERROR, "Failed to refresh Vocation Date. [vocationData=\"" + vocationData + "\"]");
            return false;
        }
    }

    private static String today() {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return simpleDateFormat.format(date);
    }
}
