/**
 * © 2019 Nokia
 *
 * Licensed under the BSD 3 Clause license
 * SPDX-License-Identifier: BSD-3-Clause
 */

package com.nokia.as.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author sebferrer
 * Date util functions
 */
public class DateUtil {

    /**
     * Gets the time range between now and <nbSeconds> ago
     *
     * @param nbSeconds
     * @return A time range (start-end)
     */
    public static Pair<Long, Long> getTimeRange(int nbSeconds) {
        Long currentTimestamp = Instant.now().getEpochSecond();

        return new Pair<>(currentTimestamp - nbSeconds, currentTimestamp);
    }

    /**
     * If the is no start time, gets the time range between <before> seconds ago and <after> seconds ago
     * If there is a start time, gets the time range between <start> + <before> and <start> + <after> seconds
     *
     * @param before
     * @param after
     * @param start
     * @return A time range (start-end)
     */
    public static Pair<Long, Long> getTimeRangeBetween(long before, long after, long start) {
        if (start == 0) {
            long currentTimestamp = Instant.now().getEpochSecond();
            return new Pair<>(currentTimestamp - after, currentTimestamp - before);
        }
        return new Pair<>(start + before, start + after);
    }

    /**
     * Gets all time ranges from <start>, splitting by the <limit>
     *
     * @param duration Duration of the global time range we want to split & get
     * @param limit
     * @param start
     * @return Collection of time ranges (start-end)
     */
    public static ArrayList<Pair<Long, Long>> getTimeRangesFromStart(int duration, int limit, long start) {
        int nbTimeRanges = (int) Math.ceil((float) duration / (float) limit);
        ArrayList<Pair<Long, Long>> timeRanges = new ArrayList<>();

        for (int i = 0; i < nbTimeRanges; i++) {
            long before = limit * i;
            long after = limit * (i + 1);
            if (duration % limit > 0) {
                after = i == nbTimeRanges - 1 ? before + duration % limit : after;
            }
            --after;
            Pair timeRange = getTimeRangeBetween(before, after, start);
            timeRanges.add(timeRange);
        }

        return timeRanges;
    }

    /**
     * Gets all time ranges from now to <duration> seconds ago, splitting by the <limit>
     *
     * @param duration Duration of the global time range we want to split & get
     * @param limit
     * @return Collection of time ranges (start-end)
     */
    public static ArrayList<Pair<Long, Long>> getHistoryTimeRanges(int duration, int limit) {
        return getTimeRangesFromStart(duration, limit, 0);
    }

    /**
     * Gets all time ranges from <start> to <end>, splitting by the <limit>
     *
     * @param start
     * @param end
     * @param limit
     * @return Collection of time ranges (start-end)
     */
    public static ArrayList<Pair<Long, Long>> getTimeRanges(long start, long end, int limit) {
        int duration = (int) (end - start) + 1;
        return getTimeRangesFromStart(duration, limit, start);
    }

    /**
     * Format a timestamp to a human date (e.g jul252019)
     *
     * @param timestamp
     * @return The formatted date
     */
    public static String formatTimestamp(long timestamp) {
        HashMap<Integer, String> monthMap = new HashMap<Integer, String>() {
            {
                put(1, "jan");
                put(2, "feb");
                put(3, "mar");
                put(4, "apr");
                put(5, "may");
                put(6, "jun");
                put(7, "jul");
                put(8, "aug");
                put(9, "sep");
                put(10, "oct");
                put(11, "nov");
                put(12, "dec");
            }
        };

        Timestamp ts = new Timestamp(timestamp * 1000L);
        Date date = new Date(ts.getTime());
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        String month = monthMap.get(localDate.getMonthValue());
        String day = localDate.getDayOfMonth() < 10 ? "0" + localDate.getDayOfMonth() : "" + localDate.getDayOfMonth();

        return month + day + localDate.getYear();
    }

    /**
     * Format a timestamp into a human duration (e.g 2h31m49s)
     *
     * @param timestamp
     * @return The formatted timestamp
     */
    public static String getTimestampDuration(long timestamp) {
        StringBuilder output = new StringBuilder();

        int day = (int) TimeUnit.SECONDS.toDays(timestamp);
        long hours = TimeUnit.SECONDS.toHours(timestamp) - (day * 24);
        long minute = TimeUnit.SECONDS.toMinutes(timestamp) - (TimeUnit.SECONDS.toHours(timestamp) * 60);
        long second = TimeUnit.SECONDS.toSeconds(timestamp) - (TimeUnit.SECONDS.toMinutes(timestamp) * 60);

        if (day > 0) {
            output.append(day + "d");
        }
        if (hours > 0) {
            output.append(hours + "h");
        }
        output.append(minute + "m")
                .append(second + "s");

        return output.toString();
    }

    /**
     * String to Date
     *
     * @param s
     * @return String to date
     */
    public static Date toDate(String s) {
        if (s.length() != 14) {
            return null;
        }

        Calendar c = Calendar.getInstance();

        c.set(
                Integer.parseInt(s.substring(0, 4)),
                Integer.parseInt(s.substring(4, 6)) - 1,
                Integer.parseInt(s.substring(6, 8)),
                Integer.parseInt(s.substring(8, 10)),
                Integer.parseInt(s.substring(10, 12)),
                Integer.parseInt(s.substring(12, 14)));

        return c.getTime();
    }

    /**
     * Checks if a date is between two others
     *
     * @param dateToCheck
     * @param startDate
     * @param endDate
     * @return true if <startDate> <= <dateToCheck> >= <endDate>
     */
    public static boolean checkBetween(Date dateToCheck, Date startDate, Date endDate) {
        return dateToCheck.compareTo(startDate) >= 0 && dateToCheck.compareTo(endDate) <= 0;
    }

    /**
     * Gets the current timestamp in milliseconds
     *
     * @return The current timestamp in milliseconds
     */
    public static long getCurrentMillis() {
        return Long.parseLong("" + Instant.now().getEpochSecond() + Instant.now().getNano() / 1000000);
    }
}