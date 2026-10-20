package org.b3log.symphony.util;

import org.b3log.symphony.model.UserExt;
import org.json.JSONObject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** Natural-day streak state; no transfers, cache or persistence side effects. */
public final class DailyCheckinMutation {
    private DailyCheckinMutation() { }
    public record Result(JSONObject changes, Integer patchStart, boolean streakReward) { }
    private static LocalDate date(final int day) { return LocalDate.parse(String.valueOf(day), DateTimeFormatter.BASIC_ISO_DATE); }
    private static int days(final LocalDate start, final LocalDate end) {
        if (start.isAfter(end)) throw new IllegalArgumentException("invalid checkin date range");
        return Math.toIntExact(ChronoUnit.DAYS.between(start, end) + 1);
    }
    public static Result apply(final JSONObject user, final LocalDate today, final long now) {
        final int day = CheckinPatchMutation.day(today);
        final JSONObject changes = new JSONObject().put(UserExt.USER_CHECKIN_TIME, now)
                .put(UserExt.USER_CURRENT_CHECKIN_STREAK_END, day);
        if (user.optInt(UserExt.USER_CURRENT_CHECKIN_STREAK_START) == 0) {
            changes.put(UserExt.USER_CURRENT_CHECKIN_STREAK_START, day).put(UserExt.USER_CURRENT_CHECKIN_STREAK, 1)
                    .put(UserExt.USER_LONGEST_CHECKIN_STREAK_START, day).put(UserExt.USER_LONGEST_CHECKIN_STREAK_END, day)
                    .put(UserExt.USER_LONGEST_CHECKIN_STREAK, 1);
            return new Result(changes, null, false);
        }
        final LocalDate oldStart = date(user.getInt(UserExt.USER_CURRENT_CHECKIN_STREAK_START));
        final LocalDate oldEnd = date(user.getInt(UserExt.USER_CURRENT_CHECKIN_STREAK_END));
        if (!oldEnd.isBefore(today) || oldStart.isAfter(oldEnd)) throw new IllegalArgumentException("invalid previous checkin run");
        final boolean adjacent = oldEnd.plusDays(1).equals(today);
        final LocalDate start = adjacent ? oldStart : today;
        final Integer patch = adjacent ? null : CheckinPatchMutation.patchStart(CheckinPatchMutation.day(oldStart), CheckinPatchMutation.day(oldEnd), today);
        final LocalDate longestStart = date(user.getInt(UserExt.USER_LONGEST_CHECKIN_STREAK_START));
        final LocalDate longestEnd = date(user.getInt(UserExt.USER_LONGEST_CHECKIN_STREAK_END));
        if (longestEnd.isAfter(today)) throw new IllegalArgumentException("future longest checkin run");
        final int streak = days(start, today), longest = days(longestStart, longestEnd);
        changes.put(UserExt.USER_CURRENT_CHECKIN_STREAK_START, CheckinPatchMutation.day(start))
                .put(UserExt.USER_CURRENT_CHECKIN_STREAK, streak).put(UserExt.USER_LONGEST_CHECKIN_STREAK, Math.max(streak, longest));
        if (streak > longest) changes.put(UserExt.USER_LONGEST_CHECKIN_STREAK_START, CheckinPatchMutation.day(start))
                .put(UserExt.USER_LONGEST_CHECKIN_STREAK_END, day);
        return new Result(changes, patch, streak % 10 == 0);
    }
}
