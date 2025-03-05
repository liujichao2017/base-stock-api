package com.hm.stock.modules.utils;

import com.hm.stock.modules.common.LogicUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

public class DateTimeUtil {
    public static Date getDate() {
        return new Date();
    }

    public static Date getDate(long day) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        instance.add(Calendar.DATE, (int) (day - 1));
        return instance.getTime();
    }

    public static boolean isCanSell(Date buyDate, int maxMinutes) {
        Long buyDateTimes = Long.valueOf(buyDate.getTime() / 1000L);

        buyDateTimes = Long.valueOf(buyDateTimes.longValue() + (maxMinutes * 60));

        Long nowDateTimes = Long.valueOf((new Date()).getTime() / 1000L);

        if (nowDateTimes.longValue() > buyDateTimes.longValue()) {
            return true;
        }
        return false;
    }

    public static boolean isWeekend(String timeZone) {
        LocalDateTime nowDateTime = getNowDateTime(timeZone);
        return nowDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || nowDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * 是否为周末, 服务器本地时区
     *
     * @return
     */
    public static boolean isWeekend() {
        LocalDateTime nowDateTime = getNowDateTime();
        return nowDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || nowDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * 是否为周末, 服务器本地时区
     *
     * @return
     */
    public static boolean isWeekend(LocalDateTime nowDateTime) {
        return nowDateTime.getDayOfWeek() == DayOfWeek.SATURDAY || nowDateTime.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public static LocalDateTime getNowDateTime(String timeZone) {
        return LocalDateTime.now(getZoneId(timeZone));
    }

    public static LocalDateTime getNowDateTime() {
        return LocalDateTime.now(ZoneId.systemDefault());
    }

    public static ZoneId getZoneId(String timeZone) {
        ZoneOffset zoneOffset = parse(timeZone);
        if (zoneOffset == null) {
            return ZoneId.systemDefault();
        }
        return zoneOffset;
    }

    public static ZoneOffset getZoneOffset(String timeZone) {
        ZoneOffset zoneOffset = parse(timeZone);

        return zoneOffset;
    }

    private static ZoneOffset parse(String timeZone) {
        return ZoneOffset.ofTotalSeconds(getTimestamp(timeZone));
    }

    public static ZoneOffset getZoneOffset(int timeZone) {
        return ZoneOffset.ofTotalSeconds(timeZone);
    }

    public static String toStr(Integer days) {
        LocalDateTime nowDateTime = getNowDateTime();
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(nowDateTime.minusDays(days));
    }

    public static String getNowDateStr(String timeZone) {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(getNowDateTime(timeZone));
    }

    public static String getNowDateStr() {
        return DateTimeFormatter.ofPattern("yyyy-MM-dd").format(getNowDateTime());
    }

    public static int getTimestamp(String timeZone) {
        String[] split = timeZone.substring(1).split(":");
        boolean plus = timeZone.startsWith("+");
        int h = Integer.parseInt(split[0]);
        int m = Integer.parseInt(split[1]);
        return ((h * 60 * 60) + (m * 60)) * (plus ? 1 : -1);
    }

    public static boolean isExpire(String timeZone, String startTime, String endTime) {
        LocalDateTime nowDateTime = getNowDateTime(timeZone);
        return isExpire(startTime, endTime, nowDateTime);
    }

    public static boolean isExpire(String startTime, String endTime) {
        LocalDateTime nowDateTime = getNowDateTime();
        return isExpire(startTime, endTime, nowDateTime);
    }

    private static boolean isExpire(String startTime, String endTime, LocalDateTime nowDateTime) {
        LocalTime nowTime = nowDateTime.toLocalTime();
        LocalTime startLocalTime = toTime(startTime);
        LocalTime endLocalTime = toTime(endTime);
        if (startLocalTime == null || endLocalTime == null) {
            return false;
        }
        //开始时间小于结束时间，正常情况
        if (startLocalTime.isBefore(endLocalTime)) {
            return nowTime.isAfter(startLocalTime) && nowTime.isBefore(endLocalTime);
        }
        //开始时间大于结束时间，非正常情况
        if (startLocalTime.isAfter(endLocalTime)) {
            return nowTime.isAfter(startLocalTime) || nowTime.isBefore(endLocalTime);
        }
        return false;
    }

    public static LocalTime toTime(String time) {
        try {
            String[] times = time.split(":");
            return LocalTime.of(Integer.parseInt(times[0]), Integer.parseInt(times[1]));
        } catch (Exception e) {
            return null;
        }
    }


    public static String toFormatStr(Long time) {
        return toFormatStr(time, "yyyy-MM-dd HH:mm");
    }


    public static Long toLong(String time) {
        return toLong(time, "yyyy-MM-dd HH:mm", "+08:00");
    }

    public static Long toLong(String time, String pattern, String zone) {
        LocalDateTime dateTime = toDateTime(time, pattern);
        return dateTime.toInstant(ZoneOffset.of(zone)).toEpochMilli();
    }

    public static LocalDateTime toDateTime(String time, String pattern) {
        return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(pattern));
    }

    public static String toFormatStr(Long time, String pattern) {
        Instant instant = Instant.ofEpochMilli(time);
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.of("+08:00"));
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

    public static Long toLong() {
        return getTimeLong("+08:00");
    }

    public static Long getTimeLong(String zone) {
        return LocalDateTime.now().toInstant(ZoneOffset.of(zone)).toEpochMilli();
    }


    public static LocalDateTime getLocalDateTime(String time) {
        if (LogicUtils.isNull(time)) {
            return LocalDateTime.now();
        }
        if (time.length() == "yyyy-MM-dd".length()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate date = LocalDate.parse(time, formatter);
            return date.atStartOfDay();
        }
        if (time.length() == "yyyy-MM-dd HH:mm".length()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime date = LocalDateTime.parse(time, formatter);
            return date;
        }
        return LocalDateTime.now();
    }

    public static LocalDateTime toDateTime(Date date) {
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static boolean isExpire(Date date) {
        LocalDateTime dateTime = toDateTime(date);
        LocalDate localDate = dateTime.toLocalDate();
        LocalDate now = LocalDate.now();

        return localDate.equals(now) || localDate.compareTo(now) <= 0;
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(isExpire(new SimpleDateFormat("yyyy-MM-dd").parse("2024-12-26")));
        System.out.println(isExpire(new SimpleDateFormat("yyyy-MM-dd").parse("2024-12-27")));
        System.out.println(isExpire(new SimpleDateFormat("yyyy-MM-dd").parse("2024-12-28")));
        System.out.println(isExpire(new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-05")));
        System.out.println(isExpire(new SimpleDateFormat("yyyy-MM-dd").parse("2025-01-06")));

    }


    public static boolean beforeCurrentTime(Date time) {
        Date date = new Date();
        return date.compareTo(time) >= 0;
    }
}
