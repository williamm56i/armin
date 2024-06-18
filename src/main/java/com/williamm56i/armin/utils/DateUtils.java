package com.williamm56i.armin.utils;

import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Slf4j
public class DateUtils {

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYYMM_SLASH = "yyyy/MM";
    public static final String YYYYMMDD_SLASH = "yyyy/MM/dd";
    public static final String YYYYMMDDHHMISS = YYYYMMDD + "HHmmss";
    public static final String YYYYMMDD_SLASH_TIME = YYYYMMDD_SLASH + " HH:mm:ss";

    /**
     * Convert Date to String
     *
     * @param date   Date
     * @param format format string
     * @return String
     */
    public static String makeDateToString(Date date, String format) {
        if (date == null) {
            return "";
        }
        if (StringUtils.isEmpty(format)) {
            format = YYYYMMDDHHMISS;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        return sdf.format(date);
    }

    /**
     * Convert date string to Date
     *
     * @param dateStr date string
     * @param format  format string
     * @return Date
     */
    public static Date makeStringToDate(String dateStr, String format) {
        if (StringUtils.isEmpty(format)) {
            format = YYYYMMDD;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        try {
            return sdf.parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Convert iso-8601 date string to Date
     *
     * @param isoDateStr iso date string
     * @return Date
     */
    public static Date isoToDate(String isoDateStr) {
        try {
            return Date.from(Instant.parse(isoDateStr));
        } catch (Exception e) {
            log.error("convert date from iso: {} fail!", isoDateStr);
            return null;
        }
    }

    /**
     * Convert iso-8601 date string to string
     *
     * @param isoDateStr iso date string
     * @param format     string format
     * @return String
     */
    public static String isoToString(String isoDateStr, String format) {
        try {
            Date date = Date.from(Instant.parse(isoDateStr));
            return makeDateToString(date, format);
        } catch (Exception e) {
            log.error("convert date format from iso: {} to {} fail!", isoDateStr, format);
            return null;
        }
    }

    /**
     * Convert date time to 0:00:00
     *
     * @param date Date
     * @return Date
     */
    public static Date convertStartDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * Convert date time to 23:59:59
     *
     * @param date Date
     * @return Date
     */
    public static Date convertEndDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
}
