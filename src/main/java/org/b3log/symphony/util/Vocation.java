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
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * 工作日/假期数据。
 * <p>
 * 数据源为 GitHub 开源项目 holiday-cn（基于国务院每年官方放假公告，每日自动更新），
 * 拉取年度 JSON 后在本地计算当天类型、下一个假期与下一个工作日，
 * 不再依赖 timor.tech 等单一第三方接口。
 * 拉取失败时保留上一次成功数据，并按普通周末规则兜底，保证前端浮窗不空白。
 * </p>
 */
@Singleton
public class Vocation {
    /**
     * Logger.
     */
    private static final Logger LOGGER = LogManager.getLogger(Vocation.class);

    final public static String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/93.0.4577.63 Safari/537.36";
    final public static JSONObject vocationData = new JSONObject();

    /**
     * holiday-cn 年度数据源（按顺序降级尝试）。
     */
    private static final String[] DATA_SOURCE_URLS = {
            "https://cdn.jsdelivr.net/gh/NateScarlet/holiday-cn@master/",
            "https://raw.githubusercontent.com/NateScarlet/holiday-cn/master/"
    };

    /**
     * 向后扫描的最大天数。
     */
    private static final int SCAN_MAX_DAYS = 366;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String[] WEEK_NAMES = {"周一", "周二", "周三", "周四", "周五", "周六", "周日"};

    /**
     * 年度假期数据缓存：year -&gt; 当年放假/调休日期列表。
     */
    private static final Map<Integer, List<HolidayDay>> YEAR_DATA_CACHE = new ConcurrentHashMap<>();
    /**
     * 缓存拉取日期：year -&gt; 拉取当天数据的日期字符串，同一天内不重复请求外部数据源。
     */
    private static final Map<Integer, String> YEAR_DATA_FETCH_DATE = new ConcurrentHashMap<>();
    /**
     * 拉锁，避免并发重复拉取年度数据。
     */
    private static final Object YEAR_DATA_LOCK = new Object();

    public void vocation(final RequestContext context) {
        context.renderJSON(vocationData);
    }

    public static boolean refresh() {
        try {
            final JSONObject computed = compute();
            if (null == computed) {
                LOGGER.log(Level.ERROR, "Failed to refresh Vocation Date. [vocationData=\"" + vocationData + "\"]");
                return false;
            }
            // 只有计算成功才覆盖共享数据；失败时保留上一次成功数据，避免前端拿到 type=-1 后首页空白
            vocationData.put("date", computed.optString("date"));
            vocationData.put("type", computed.optInt("type", -1));
            vocationData.put("dayName", computed.optString("dayName"));
            vocationData.put("vName", computed.optString("vName"));
            vocationData.put("vRest", computed.optInt("vRest", -1));
            vocationData.put("wRest", computed.optInt("wRest", -1));
            System.out.println(">>> Vocation Date has refreshed. [vocationData=\"" + vocationData + "\"]");
            return true;
        } catch (final Exception e) {
            LOGGER.log(Level.ERROR, "Failed to refresh Vocation Date. [vocationData=\"" + vocationData + "\"]", e);
            return false;
        }
    }

    /**
     * 计算当天的假期/工作日信息。
     *
     * @return 计算结果，永远不会返回 {@code null}（最差情况按周末规则兜底）
     */
    private static JSONObject compute() {
        final LocalDate today = LocalDate.now();
        final JSONObject result = new JSONObject();
        result.put("date", DATE_FORMATTER.format(today));

        final int weekValue = today.getDayOfWeek().getValue(); // 1=周一 ... 7=周日
        final String weekName = WEEK_NAMES[weekValue - 1];
        final boolean weekend = weekValue >= 6;

        final List<HolidayDay> yearDays = ensureYearData(today.getYear());
        final HolidayDay todayEntry = null == yearDays ? null : findDay(yearDays, today);

        final int type;
        final String dayName;
        if (null != todayEntry && todayEntry.offDay) {
            // 法定节假日或被并入假期的周末
            type = 2;
            dayName = todayEntry.name;
        } else if (null != todayEntry) {
            // 调休上班日
            type = 3;
            dayName = weekName;
        } else if (weekend) {
            type = 1;
            dayName = weekName;
        } else {
            type = 0;
            dayName = weekName;
        }
        result.put("type", type);
        result.put("dayName", dayName);

        if (1 == type || 2 == type) {
            // 休息日：距离下一个工作日还有几天
            final LocalDate nextWorkday = findNext(today, date -> !isRestDay(date));
            result.put("wRest", null == nextWorkday ? -1 : ChronoUnit.DAYS.between(today, nextWorkday));
            result.put("vName", "");
            result.put("vRest", -1);
        } else {
            // 工作日/调休：距离下一个休息日还有几天（含普通周末）
            final LocalDate nextRestDay = findNext(today, Vocation::isRestDay);
            if (null == nextRestDay) {
                result.put("vName", "");
                result.put("vRest", -1);
            } else {
                final HolidayDay restEntry = lookupDay(nextRestDay);
                result.put("vName", null == restEntry ? WEEK_NAMES[nextRestDay.getDayOfWeek().getValue() - 1] : restEntry.name);
                result.put("vRest", ChronoUnit.DAYS.between(today, nextRestDay));
            }
            result.put("wRest", -1);
        }

        if (null == yearDays) {
            LOGGER.log(Level.WARN, "holiday-cn data unavailable, vocation computed by weekend rules only. [vocationData=\"" + result + "\"]");
        }
        return result;
    }

    /**
     * 从 today+1 开始向后查找第一个满足条件的日期。
     */
    private static LocalDate findNext(final LocalDate today, final Predicate<LocalDate> matcher) {
        for (int i = 1; i <= SCAN_MAX_DAYS; i++) {
            final LocalDate date = today.plusDays(i);
            if (matcher.test(date)) {
                return date;
            }
        }
        return null;
    }

    /**
     * 判断指定日期是否休息日：优先用 holiday-cn 数据，缺数据时按普通周末规则。
     */
    private static boolean isRestDay(final LocalDate date) {
        final HolidayDay day = lookupDay(date);
        if (null != day) {
            return day.offDay;
        }
        return date.getDayOfWeek().getValue() >= 6;
    }

    private static HolidayDay lookupDay(final LocalDate date) {
        final List<HolidayDay> days = ensureYearData(date.getYear());
        return null == days ? null : findDay(days, date);
    }

    private static HolidayDay findDay(final List<HolidayDay> days, final LocalDate date) {
        for (final HolidayDay day : days) {
            if (day.date.equals(date)) {
                return day;
            }
        }
        return null;
    }

    /**
     * 获取指定年度的假期数据（带当日缓存），拉取失败返回 {@code null}。
     */
    private static List<HolidayDay> ensureYearData(final int year) {
        final String todayStr = DATE_FORMATTER.format(LocalDate.now());
        final List<HolidayDay> cached = YEAR_DATA_CACHE.get(year);
        if (null != cached && todayStr.equals(YEAR_DATA_FETCH_DATE.get(year))) {
            return cached;
        }
        synchronized (YEAR_DATA_LOCK) {
            final List<HolidayDay> rechecked = YEAR_DATA_CACHE.get(year);
            if (null != rechecked && todayStr.equals(YEAR_DATA_FETCH_DATE.get(year))) {
                return rechecked;
            }
            final JSONObject yearJson = fetchYearJson(year);
            if (null == yearJson) {
                // 拉取失败时保留旧缓存（如果有），不要清空
                return rechecked;
            }
            final List<HolidayDay> days = parseYearData(yearJson);
            YEAR_DATA_CACHE.put(year, days);
            YEAR_DATA_FETCH_DATE.put(year, todayStr);
            return days;
        }
    }

    private static JSONObject fetchYearJson(final int year) {
        for (final String base : DATA_SOURCE_URLS) {
            try {
                final HttpResponse response = HttpRequest.get(base + year + ".json")
                        .connectionTimeout(10000).timeout(30000).header("User-Agent", UA)
                        .send();
                if (200 == response.statusCode()) {
                    response.charset("UTF-8");
                    final JSONObject result = new JSONObject(response.bodyText());
                    final JSONArray days = result.optJSONArray("days");
                    if (null != days) {
                        if (0 == days.length()) {
                            LOGGER.log(Level.INFO, "holiday-cn [" + year + ".json] has no published holiday data yet (official announcement pending), weekend rules will be applied for this year.");
                        }
                        return result;
                    }
                }
                LOGGER.log(Level.WARN, "Failed to fetch holiday data from [" + base + year + ".json], status [" + response.statusCode() + "]");
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Failed to fetch holiday data from [" + base + year + ".json]", e);
            }
        }
        return null;
    }

    private static List<HolidayDay> parseYearData(final JSONObject yearJson) {
        final JSONArray days = yearJson.optJSONArray("days");
        final List<HolidayDay> list = new ArrayList<>();
        for (int i = 0; i < days.length(); i++) {
            final JSONObject day = days.optJSONObject(i);
            if (null == day) {
                continue;
            }
            try {
                list.add(new HolidayDay(
                        LocalDate.parse(day.optString("date"), DATE_FORMATTER),
                        day.optString("name"),
                        day.optBoolean("isOffDay")));
            } catch (final Exception e) {
                LOGGER.log(Level.WARN, "Skipped an invalid holiday day entry. [entry=\"" + day + "\"]");
            }
        }
        list.sort(Comparator.comparing(day -> day.date));
        return list;
    }

    /**
     * 单条放假/调休记录。
     */
    private static final class HolidayDay {

        /**
         * 日期.
         */
        final LocalDate date;
        /**
         * 假期名称，如"国庆节".
         */
        final String name;
        /**
         * true 表示放假，false 表示调休上班.
         */
        final boolean offDay;

        HolidayDay(final LocalDate date, final String name, final boolean offDay) {
            this.date = date;
            this.name = name;
            this.offDay = offDay;
        }
    }
}
