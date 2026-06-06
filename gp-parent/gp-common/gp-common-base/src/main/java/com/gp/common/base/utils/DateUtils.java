package com.gp.common.base.utils;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.lang.management.ManagementFactory;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * 时间工具类
 *
 * @author ruoyi
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {
    public static String YYYY = "yyyy";

    public static String YYYY_MM = "yyyy-MM";

    public static String YYYY_MM_DD = "yyyy-MM-dd";

    public static String YYYYMMDD = "yyyyMMdd";

    public static String YYMMDD = "yyMMdd";

    public static String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static String YYYY_MM_DD_T_HH_MM_SS_SSSSSSSX = "yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX";
    public static String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static String YYYY_MM_DD_T_HH_MM_SS_Z = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static String YYYY_MM_DD_T_HH_MM_SS_UZ = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    public static String HH_MM_SS = "HH:mm:ss";

    public static String YYYY_MM_DD_HH_MM_SS_SSSS = "yyyy-MM-dd HH:mm:ss.ssss";
    private static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    private static final DateTimeFormatter FORMATTERS = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    /**
     * 时区定义
     */
    public static String GMT_DECREASE_4 = "GMT-4";

    /**
     * 时区定义
     */
    public static String GMT_ADD_8 = "GMT+8";

    /**
     * 处理时间格式 2021-06-26T12:11:52.000+0000 为 yyyy-MM-dd HH:mm:ss
     */
    @SneakyThrows
    public static String reduceDateByString(String oldDate, Long r) {
        DateFormat df = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
        Long oldTime = df.parse(oldDate).getTime();
        Long oldChangeTime = oldTime - r;
        SimpleDateFormat df1 = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS);
        return df1.format(oldChangeTime);
    }

    @SneakyThrows
    public static Long startFormatDateTime(String format) {
        String start = format + " 00:00:00";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(start).getTime();
    }

    @SneakyThrows
    public static Long endFormatDateTime(String format) {
        String end = format + " 23:59:59";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.parse(end).getTime();
    }

    public static String formatYYYYMMDD(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD);
        return formatter.format(time);
    }

    public static String formatYYYYMMDD(long time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.format(time);
    }

    public static String getUTCFormatToString() {
        DateTimeFormatter df = DateTimeFormatter.ofPattern(YYYY_MM_DD_HH_MM_SS);
        LocalDateTime localDateTime = LocalDateTime.now(ZoneOffset.UTC);
        return df.format(localDateTime);
    }

    @SneakyThrows
    public static Long GMT8ToUtcLocalTime(final String time) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(YYYY_MM_DD_T_HH_MM_SS_Z);
        LocalDateTime localDateTime = LocalDateTime.parse(time, dateTimeFormatter);
        String utcString = localDateTime.atOffset(ZoneOffset.ofHours(8)).withOffsetSameInstant(ZoneOffset.UTC).toInstant().toString();
        SimpleDateFormat formatter = new SimpleDateFormat(YYYY_MM_DD_T_HH_MM_SS_UZ);
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return formatter.parse(utcString).getTime();
    }

    /**
     * 根据时区转成特定时间
     *
     * @param Zone     要转换的时区
     * @param dateTime 转换的时间
     * @return
     */
    public static String conversionZone(String Zone, Date dateTime, String DateType) {
        //先转换成定制的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateType);
        simpleDateFormat.setTimeZone(getTimeZone(Zone));
        return simpleDateFormat.format(dateTime);
    }

    /**
     * 根据时区转成特定时间
     *
     * @param Zone     要转换的时区
     * @param dateTime 转换的时间
     * @return
     */
    public static Date conversionZoneToDate(String Zone, Date dateTime, String DateType) {
        //先转换成定制的时间
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DateType);
        simpleDateFormat.setTimeZone(getTimeZone(Zone));
        String dataStr = simpleDateFormat.format(dateTime);
        try {
            return parseDate(dataStr, YYYY_MM_DD_HH_MM_SS_SSSS);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前Date型日期
     *
     * @return Date() 当前日期
     */
    public static Date getNowDate() {
        return new Date();
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDate() {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getYesterdayDateYYYMMDD() {
        return parseDateToStr(YYYY_MM_DD, new Date());
    }

    public static String getDateYYYMMDD(Date date) {
        return parseDateToStr(YYYYMMDD, date);
    }

    public static String getDateMillisecond() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS_SSSS);
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getDateTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String getTime() {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static final String dateTimeNow() {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static Date getyyyyMMddHHmmssDate(String dateString) {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        try {
            date = formatter.parse(dateString);
            System.out.println("Parsed Date: " + date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static boolean checkTimestampMinute(long timestamp, long num) {
        return checkTimestampMinute(new Date(timestamp), num);
    }

    public static boolean checkTimestampMinute(Date checkDate, long num) {
        long diffMinutes = DateUtil.between(checkDate, new Date(), DateUnit.MINUTE);
        return diffMinutes < num;
    }

    public static final String dateTimeNow(final String format) {
        return parseDateToStr(format, new Date());
    }

    public static final String dateTime(final String format, Date date) {
        return parseDateToStr(format, date);
    }

    public static final String formatDate(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static final String parseDateToStr(final String format, final Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 解析日期默认格式
     *
     * @param date 日期
     * @return {@link String}
     */
    public static final String parseDateToDefaultStr(final Date date) {
        return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).format(date);
    }

    /**
     * 解析日期默认时间格式
     *
     * @param date 日期
     * @return {@link String}
     */
    public static final String parseDateToDefaultTimeStr(final Date date) {
        return new SimpleDateFormat(HH_MM_SS).format(date);
    }

    /**
     * 格式化日期
     *
     * @param format 格式
     * @param ts     ts
     * @return {@link Date}
     */
    public static final Date formatDate(final String format, final String ts) {
        try {
            return new SimpleDateFormat(format).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 格式化日期
     * yyyy-MM-dd HH:mm:ss
     *
     * @param ts 时间字符串
     * @return {@link Date}
     */
    public static final Date formatDate(final String ts) {
        try {
            return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(ts);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static final String datePath() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy/MM/dd");
    }

    /**
     * 得到的天的秒数
     *
     * @param day 一天
     * @return {@link String}
     */
    public static final Integer getMillSecondOfDay(Integer day) {
        return day * 24 * 60 * 60 * 1000;
    }

    public static final String getDateStringByDateAndDay(Date date, int day) {
        Integer millSecondOfDay = getMillSecondOfDay(day);
        Date date1 = new Date(date.getTime() + millSecondOfDay);
        return DateFormatUtils.format(date1, YYYY_MM_DD_HH_MM_SS);
    }

    /**
     * 获取给定时间的开始结束时间一周 -今天23:59:59
     *
     * @param date 日期
     * @return {@link String[]}
     */
    public static final String[] getWeekDate(Date date) {
        if (null == date) {
            return null;
        }
        Date now = new Date(date.getTime() - getMillSecondOfDay(6));
        String sDay = DateFormatUtils.format(now, "yyyy-MM-dd");
        String eDay = DateFormatUtils.format(date, "yyyy-MM-dd");
        String star = sDay + " 00:00:00";
        String end = eDay + " 23:59:59";
        return new String[]{star, end};
    }

    /**
     * 获取给定时间的开始结束时间一周 -今天23:59:59
     *
     * @param date 日期
     * @return {@link String[]}
     */
    public static final String[] getWeekDate2(Date date) {
        if (null == date) {
            return null;
        }
        Date now = new Date(date.getTime() - getMillSecondOfDay(6));
        String sDay = DateFormatUtils.format(now, "yyyy-MM-dd");
        String eDay = DateFormatUtils.format(date, "yyyy-MM-dd");
        return new String[]{sDay, eDay};
    }

    /**
     * 获取最近一周的开始结束时间 -昨天23:59:59
     *
     * @return {@link String[]}
     */
    public static final String[] getYesterdayWeekDate() {
        Date date = new Date();
        Date s = new Date(date.getTime() - getMillSecondOfDay(7));
        Date e = new Date(date.getTime() - getMillSecondOfDay(1));
        String sDay = DateFormatUtils.format(s, "yyyy-MM-dd");
        String eDay = DateFormatUtils.format(e, "yyyy-MM-dd");
        String star = sDay + " 00:00:00";
        String end = eDay + " 23:59:59";
        return new String[]{star, end};
    }

    /**
     * 获取最近一周的开始结束时间 -昨天23:59:59
     *
     * @return {@link String[]}
     */
    public static final String[] getTodayWeekDate() {
        return getWeekDate(new Date());
    }

    /**
     * 获取最近昨天的开始结束时间
     *
     * @return {@link String[]}
     */
    public static final String[] getYesterdayDate() {
        Date now = new Date();
        Date yes = new Date(now.getTime() - getMillSecondOfDay(1));
        String sDay = DateFormatUtils.format(yes, "yyyy-MM-dd");
        String eDay = DateFormatUtils.format(yes, "yyyy-MM-dd");
        String star = sDay + " 00:00:00";
        String end = eDay + " 23:59:59";
        return new String[]{star, end};
    }

    /**
     * 获取今天的开始结束时间
     *
     * @return {@link String[]}
     */
    public static final String[] getTodayDate() {
        Date now = new Date();
        String day = DateFormatUtils.format(now, "yyyy-MM-dd");
        String star = day + " 00:00:00";
        String end = day + " 23:59:59";
        return new String[]{star, end};
    }

    /**
     * 获取今天的开始结束时间
     *
     * @return {@link String[]}
     */
    public static final String getTodayDate2() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyy-MM-dd");
    }

    /**
     * 获取指定时间的开始结束时间
     *
     * @return {@link String[]}
     */
    public static final String[] getStartAndEndDate(String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date parse = simpleDateFormat.parse(time);
            String day = DateFormatUtils.format(parse, "yyyy-MM-dd");
            String star = day + " 00:00:00";
            String end = day + " 23:59:59";
            return new String[]{star, end};
        } catch (Exception e) {
            return new String[]{};
        }
    }

    /**
     * 获取指定时间的开始结束时间
     *
     * @return {@link String[]}
     */
    public static final String[] getStartAndEndByYYYYMMdd(String dateStr) {
        try {
            String star = dateStr + " 00:00:00";
            String end = dateStr + " 23:59:59";
            return new String[]{star, end};
        } catch (Exception e) {
            return new String[]{};
        }
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static final String formatDate() {
        Date now = new Date();
        return DateFormatUtils.format(now, "yyyyMMdd");
    }

    /**
     * 获取周几
     * 0是周日 0-6
     *
     * @return {@link Integer}
     */
    public static Integer getWeek() {
        int week = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1;
        return week == 0 ? 7 : week;
    }

    /**
     * 获取周几
     * 0是周日 0-6
     *
     * @return {@link Integer}
     */
    public static String getWeekChinese(int week) {
        if (week > 0 && week <= 7) {
            String[] strings = {"一", "二", "三", "四", "五", "六", "日"};
            return strings[week - 1];
        } else {
            return "";
        }
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取服务器启动时间
     */
    public static Date getServerStartDate() {
        long time = ManagementFactory.getRuntimeMXBean().getStartTime();
        return new Date(time);
    }

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }

    /**
     * 获取对应时间的时分秒
     *
     * @param date 日期
     * @return {@link String}
     */
    public static String getTimeString(Date date) {
        return DateFormatUtils.format(date, "HH:mm:ss");
    }

    public static String getDateString(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd");
    }

    /**
     * Delta 版本
     * 格式：yyMMddHHmmssfff
     * 范例: 171219001225452
     *
     * @return {@link String}
     */
    public static String getDelta() {
        return DateTime.now().toString("yyMMddHHmmssSSS");
    }

    /**
     * 将北京时区的时间转化为当前系统对应时区的时间
     *
     * @param beijingTime
     * @param format
     * @return
     */
    public static String beijingTime2PhoneTime(String beijingTime, String format) {
        Date beijingDate = parseToDate(beijingTime, format);
        Date phoneDate = changeTimeZone(beijingDate, getBeijingTimeZone(), getSystemTimeZone());
        String phoneTime = formatDateToStr(phoneDate, format);
        return phoneTime;
    }

    /**
     * 将日期字符串转换为Date对象
     *
     * @param date   日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
     * @param format 格式化字符串
     * @return 日期字符串的Date对象表达形式
     */
    public static Date parseToDate(String date, String format) {
        Date dt = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        try {
            dt = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dt;
    }

    /**
     * 获取更改时区后的时间
     *
     * @param date    时间
     * @param oldZone 旧时区
     * @param newZone 新时区
     * @return 时间
     */
    public static Date changeTimeZone(Date date, TimeZone oldZone, TimeZone newZone) {
        Date dateTmp = null;
        if (date != null) {
            int timeOffset = oldZone.getRawOffset() - newZone.getRawOffset();
            dateTmp = new Date(date.getTime() - timeOffset);
        }
        return dateTmp;
    }

    /**
     * 将date----->String
     * 将Date对象转换为指定格式的字符串
     *
     * @param date     Date对象
     * @param //String format 格式化字符串
     * @return Date对象的字符串表达形式
     */
    public static String formatDateToStr(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 获取北京时区
     *
     * @return
     */
    public static TimeZone getBeijingTimeZone() {
        return TimeZone.getTimeZone("GMT+8:00");
    }

    /**
     * 获取柏林时区
     *
     * @return
     */
    public static TimeZone getBolinTimeZone() {
        return TimeZone.getTimeZone("GMT-4:00");
    }

    /**
     * 获取所需时区
     *
     * @return
     */
    public static TimeZone getTimeZone(String timeZone) {
        return TimeZone.getTimeZone(timeZone);
    }

    /**
     * 获取当前手机对应的系统时区
     */
    public static TimeZone getSystemTimeZone() {
        return TimeZone.getDefault();
    }

    /**
     * 将本地系统对应时区的时间转换为上北京时区对应的时间
     *
     * @param time 转换的字符串时间
     * @return
     */
    public static String systemTimeToBeijingTime(String time) {
        Date phoneDate = parseToDate(time, YYYY_MM_DD_HH_MM_SS_SSSS);
        Date beijingDate = changeTimeZone(phoneDate, getSystemTimeZone(), getBeijingTimeZone());
        String beijingTime = formatDateToStr(beijingDate, YYYY_MM_DD_HH_MM_SS_SSSS);
        return beijingTime;
    }

    /**
     * 将北京时区对应的时间转换为上本地系统对应时区的时间
     *
     * @param time 转换的字符串时间
     * @return
     */
    public static String beijingTimeToSystemTime(String time) {
        Date phoneDate = parseToDate(time, YYYY_MM_DD_HH_MM_SS_SSSS);
        Date beijingDate = changeTimeZone(phoneDate, getBeijingTimeZone(), getSystemTimeZone());
        String beijingTime = formatDateToStr(beijingDate, YYYY_MM_DD_HH_MM_SS_SSSS);
        return beijingTime;
    }

    /**
     * 系统时间转西四区时间
     *
     * @param time 转换的字符串时间
     * @return
     */
    public static Date systemTimeToX4(String time, String format) {
        Date sDate = parseToDate(time, format);
        Date x4Date = changeTimeZone(sDate, getSystemTimeZone(), getBolinTimeZone());
        return x4Date;
    }

    /**
     * 将本地系统对应时区的时间转换为上北京时区对应的时间
     *
     * @param date 转换时间
     * @return
     */
    public static String phoneDateBeijingStringTime(Date date) {
        Date beijingDate = changeTimeZone(date, getSystemTimeZone(), getBeijingTimeZone());
        String beijingTime = formatDateToStr(beijingDate, YYYY_MM_DD_HH_MM_SS_SSSS);
        return beijingTime;
    }

    public static String formatImDateToStr(String eventDate) {
        SimpleDateFormat bolinSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSX");
        bolinSimpleDateFormat.setTimeZone(getBolinTimeZone());
        SimpleDateFormat beijingSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        beijingSimpleDateFormat.setTimeZone(getBeijingTimeZone());
        Date parse = null;
        try {
            parse = bolinSimpleDateFormat.parse(eventDate);
            return beijingSimpleDateFormat.format(parse);
        } catch (Exception e) {
//            e.printStackTrace();
            return eventDate;
        }
    }

    /**
     * 指定源时区时间戳  转 目标时区的时间戳
     *
     * @param sourceTime     源时区时间戳
     * @param sourceTimezone 源时区
     * @param targetTimezone 目标时区
     * @return
     */
    public static Long convertTimezoneToLong(Long sourceTime, String sourceTimezone, String targetTimezone) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone(sourceTimezone));
        // 设置之后,calendar会计算各种filed对应的值,并保存
        calendar.setTimeInMillis(sourceTime);

        //获取源时区的到UTC的时区差
        int sourceZoneOffset = calendar.get(Calendar.ZONE_OFFSET);

        calendar.setTimeZone(TimeZone.getTimeZone(targetTimezone));
        int targetZoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int targetDaylightOffset = calendar.get(Calendar.DST_OFFSET); // 夏令时

        long targetTime = sourceTime + (targetZoneOffset + targetDaylightOffset) - sourceZoneOffset;

        return targetTime;
    }

    /**
     * 默认系统时区的时间戳转目标时区的时间戳
     *
     * @param sourceTime     当前系统时区的时间
     * @param targetTimezone 目标时区
     * @return
     */
    public static Long convertTimezoneToLong(Long sourceTime, String targetTimezone) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(sourceTime);
        calendar.setTimeZone(TimeZone.getTimeZone(targetTimezone));

        //获取源时区的到UTC的时区差
        int sourceZoneOffset = calendar.get(Calendar.ZONE_OFFSET);

        int targetZoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int targetDaylightOffset = calendar.get(Calendar.DST_OFFSET); // 夏令时

        long targetTime = sourceTime + (targetZoneOffset + targetDaylightOffset) - sourceZoneOffset;

        return targetTime;

    }

    /**
     * 获取指定时区的 当前时间戳
     *
     * @param targetTimezone
     * @return
     */
    public static Long getTimeByimezone(String targetTimezone) {

        Calendar calendar = Calendar.getInstance();
        //获取源时区的到UTC的时区差
        int sourceZoneOffset = calendar.get(Calendar.ZONE_OFFSET);

        calendar.setTimeZone(TimeZone.getTimeZone(targetTimezone));

        int targetZoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int targetDaylightOffset = calendar.get(Calendar.DST_OFFSET); // 夏令时

        long targetTime = System.currentTimeMillis() + (targetZoneOffset + targetDaylightOffset) - sourceZoneOffset;

        return targetTime;
    }

    /**
     * 根据传入天数，获取时间段日期
     *
     * @param dayInt
     * @return
     */
    public static String[] getDateByNum(Integer dayInt) {
        long dTime = 1000 * 3600 * 24 * Long.parseLong(dayInt.toString());
        long now = System.currentTimeMillis();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String startTime = df.format(now - dTime);
        df = new SimpleDateFormat("yyyy-MM-dd 23:59:59");
        String endTime = df.format(now);
        return new String[]{startTime, endTime};
    }

    public static String getGMT4DateStr() {
        Long timeByimezone = getTimeByimezone(GMT_DECREASE_4);
        String formatDateToStr = formatDateToStr(new Date(timeByimezone), YYMMDD);
        return formatDateToStr;
    }

    public static String getGMT8DateStr() {
        String formatDateToStr = formatDateToStr(new Date(), YYMMDD);
        return formatDateToStr;
    }

    public static boolean validDateStr(String dateStr, String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            pattern = YYYY_MM_DD_HH_MM_SS;
        }
        try {
            LocalDate.parse(dateStr, new DateTimeFormatterBuilder().appendPattern(pattern).parseStrict().toFormatter());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取2个日期中间的所有日期yyyy-mm-dd
     *
     * @return
     */
    public static List<String> getAllDateFromStartDateToEndDate(String startDate, String endDate) {
        List<String> dates = new ArrayList<>();
        Date sDate = parseToDate(startDate, YYYY_MM_DD);
        Date eDate = parseToDate(endDate, YYYY_MM_DD);
        Long sLongDate = sDate.getTime();
        while (true) {
            dates.add(DateUtils.formatYYYYMMDD(sLongDate));
            if (sLongDate >= eDate.getTime()) {
                break;
            }
            sLongDate += 24 * 3600 * 1000;
        }
        return dates;
    }

    /**
     * 获取系统当前秒数
     *
     * @return
     */
    public static int getSecond() {
        int second = Calendar.getInstance().get(Calendar.SECOND);
        return second;
    }

    /**
     * 获取时间中的数值
     *
     * @return
     */
    public static int[] getDateNumArry() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);//获取年份
        int month = cal.get(Calendar.MONTH);//获取月份
        int day = cal.get(Calendar.DATE);//获取日
        int hour = cal.get(Calendar.HOUR);//小时
        int minute = cal.get(Calendar.MINUTE);//分
        int second = cal.get(Calendar.SECOND);//秒
        return new int[]{year, month, day, hour, minute, second};
    }

    public static long getDateForLong(Integer type, Integer num) {
        Calendar cal = Calendar.getInstance();
        cal.add(type, num);
        cal.set(Calendar.HOUR, 0);//小时
        cal.set(Calendar.MINUTE, 0);//分
        cal.set(Calendar.SECOND, 0);//秒
        return cal.getTime().getTime();
    }

    public static long addDateForType(Date date, Integer type, Integer num) {
        Calendar cal = Calendar.getInstance();
        cal.add(type, num);
        cal.set(Calendar.HOUR, 0);//小时
        cal.set(Calendar.MINUTE, 0);//分
        cal.set(Calendar.SECOND, 0);//秒
        return cal.getTime().getTime();
    }

    /**
     * 获取最近7天的开始结束时间
     *
     * @return {@link String[]}
     */
    public static final String[] getSevenDayDate() {
        Date date = new Date();
        Date s = new Date(date.getTime() - getMillSecondOfDay(6));
        String sDay = DateFormatUtils.format(s, "yyyy-MM-dd");
        String eDay = DateFormatUtils.format(date, "yyyy-MM-dd");
        return new String[]{sDay, eDay};
    }

    /**
     * 获取昨天日期, 默认格式为yyyy-MM-dd
     *
     * @return String
     */
    public static String getYesDate() {
        return dateTime(YYYY_MM_DD, getYesterday(new Date()));
    }

    public static Date getYesterday(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(calendar.DATE, -1);
        return calendar.getTime();
    }

    /**
     * 获取本月日期
     *
     * @return {@link String[]}
     */
    public static final String[] getMonthDayDate() {
        Date date = new Date();
        String month = parseDateToStr(YYYY_MM, date);
        String sDay = getMinDateMonth(month);
        String eDay = getMaxDateMonth(month);

        return new String[]{sDay, eDay};
    }

    /*
       输入日期字符串比如2017-03，返回当月第一天的Date
    */
    public static String getMinDateMonth(String month) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM);
            Date nowDate = sdf.parse(month);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowDate);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
            return dateTime(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
        输入日期字符串，返回当月最后一天的Date
    */
    public static String getMaxDateMonth(String month) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM);
            Date nowDate = sdf.parse(month);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(nowDate);
            calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
            return dateTime(calendar.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String dateTime(final Date date) {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    /**
     * 获取本年日期
     *
     * @return {@link String[]}
     */
    public static final String[] getYearDayDate() {
        Date date = new Date();
        String year = parseDateToStr(YYYY, date);
        String sDay = year + "-01-01";
        String eDay = year + "-12-31";
        return new String[]{sDay, eDay};
    }

    /**
     * 计算两个时间差(是否大于一周)
     */
    public static Boolean getDatePoor7(String endDate, String nowDate) {
        long nd = 1000 * 24 * 60 * 60;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            // long ns = 1000;
            // 获得两个时间的毫秒时间差异
            long diff = sdf.parse(endDate).getTime() - sdf.parse(nowDate).getTime();
            long l = nd * 7;
            return diff > l;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 计算两个时间差(是否大于一个月)
     */
    public static Boolean getDatePoor30(String endDate, String nowDate) {
        long nd = 1000L * 24 * 60 * 60; // 一天的毫秒数
        long oneMonth = nd * 30;        // 30 天的毫秒数

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
            // 计算两个时间的毫秒差异
            long diff = sdf.parse(endDate).getTime() - sdf.parse(nowDate).getTime();
            return diff > oneMonth;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static Date transitionZZ(String inputDate) {
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
        Date date = new Date();
        try {
            date = inputFormatter.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 对日期的【秒】进行加/减
     *
     * @param date    日期
     * @param seconds 秒数，负数为减
     * @return 加/减几秒后的日期
     */
    public static Date addDateSeconds(Date date, int seconds) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.SECOND, seconds); // 在当前时间上加上10秒 dateTime.offset(seconds).toDate();
    }

    /**
     * 对日期的【分钟】进行加/减
     *
     * @param date    日期
     * @param minutes 分钟数，负数为减
     * @return 加/减几分钟后的日期
     */
    public static Date addDateMinutes(Date date, int minutes) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.MINUTE, minutes); // 在当前时间上加上3分钟
    }

    /**
     * 对日期的【小时】进行加/减
     *
     * @param date  日期
     * @param hours 小时数，负数为减
     * @return 加/减几小时后的日期
     */
    public static Date addDateHours(Date date, int hours) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.HOUR, hours);
    }

    /**
     * 对日期的【天】进行加/减
     *
     * @param date 日期
     * @param days 天数，负数为减
     * @return 加/减几天后的日期
     */
    public static Date addDateDays(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.DAY_OF_MONTH, days); // 在当前时间上加上7天
    }

    public static Date addDateDays00(Date date, int days) {
        DateTime dateTime = new DateTime(date);
        DateTime offset = dateTime.offset(DateField.DAY_OF_MONTH, days);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        String format = df.format(offset);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
        try {
            return sdf.parse(format);
        } catch (ParseException e) {
            return new Date();
        }
    }

    /**
     * 对日期的【周】进行加/减
     *
     * @param date  日期
     * @param weeks 周数，负数为减
     * @return 加/减几周后的日期
     */
    public static Date addDateWeeks(Date date, int weeks) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.WEEK_OF_YEAR, weeks); // 在当前时间上加上2周
    }

    /**
     * 对日期的【月】进行加/减
     *
     * @param date   日期
     * @param months 月数，负数为减
     * @return 加/减几月后的日期
     */
    public static Date addDateMonths(Date date, int months) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.MONTH, months); // 在当前时间上加上3个月
    }

    /**
     * 对日期的【年】进行加/减
     *
     * @param date  日期
     * @param years 年数，负数为减
     * @return 加/减几年后的日期
     */
    public static Date addDateYears(Date date, int years) {
        DateTime dateTime = new DateTime(date);
        return dateTime.offset(DateField.YEAR, years); // 在当前时间上加上1年
    }

    /**
     * 给一个日期返回开始时间
     *
     * @return {@link String[]}
     */
    public static Date getStartTimeByDate(Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String day = DateFormatUtils.format(date, "yyyy-MM-dd");
            String star = day + " 00:00:00";
            return simpleDateFormat.parse(star);
        } catch (Exception e) {

        }
        return new Date();
    }

    /**
     * 给一个日期返回结束
     *
     * @return {@link String[]}
     */
    public static Date getEndTimeByDate(Date date) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String day = DateFormatUtils.format(date, "yyyy-MM-dd");
            String end = day + " 23:59:59";
            return simpleDateFormat.parse(end);
        } catch (Exception e) {

        }
        return new Date();
    }

    /**
     * 获取传入日期的结束时间
     *
     * @param dayStr yyyy-MM-dd
     * @return yyyy-MM-dd 23:59:59
     */
    public static Date getEndTimeByDate(String dayStr) {
        return DateUtils.getEndTimeByDate(DateUtils.parseDate(dayStr));
    }

    /**
     * 根据 根据给定的 数字 算出 加减月份
     *
     * @return {@link String[]}
     */
    public static Date addMouth(Integer mouth) {
        try {
            Calendar calendar = Calendar.getInstance(); // 获取当前日期
            calendar.add(Calendar.MONTH, mouth); // 将月份减去1个月
            // 输出一个月前的日期
            return calendar.getTime();
        } catch (Exception e) {

        }
        return new Date();
    }

    /**
     * 转换时间0全部 1 今天, 2 七天 3一个月 4三个月
     *
     * @param timeSection
     */
    public static HashMap<String, Date> changeTimeSection(Integer timeSection) {
        HashMap<String, Date> map = new HashMap<>();
        Date startTime = null;
        if (timeSection == null) {
            timeSection = 0;
        }
        //结束时间都是最后
        Date endTime = DateUtils.getEndTimeByDate(new Date());
        switch (timeSection) {

            case 1:
                // 今天
                startTime = DateUtils.getStartTimeByDate(new Date());
                break;
            case 2:
                // 七天
                startTime = DateUtils.getStartTimeByDate(DateUtils.addDays(new Date(), -7));
                break;
            case 3:
                // 一个月
                startTime = DateUtils.getStartTimeByDate(DateUtils.addMouth(-1));
                break;
            case 4:
                // 三个月
                startTime = DateUtils.getStartTimeByDate(DateUtils.addMouth(-3));
                break;
            case 5:
                // 昨天
                startTime = DateUtils.getStartTimeByDate(DateUtils.addDays(new Date(), -1));
                endTime = DateUtils.getEndTimeByDate(DateUtils.addDays(new Date(), -1));
                break;
            case 6:
                // 当月第一天
                Calendar calendar = Calendar.getInstance();

                // 设置日期为当月的第一天
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                // 从Calendar对象获取Date实例
                Date firstDayOfMonth = calendar.getTime();
                startTime = DateUtils.getStartTimeByDate(firstDayOfMonth);
                break;
            default:
                break;
        }

        map.put("startTime", startTime);
        map.put("endTime", endTime);

        return map;
    }

    /**
     * 获取月份时间范围 0 本月, 1 上一个月
     *
     * @param monthType 月份类型
     * @return 包含开始时间和结束时间的Map
     */
    public static HashMap<String, Date> getMonthTimeRange(Integer monthType) {
        HashMap<String, Date> map = new HashMap<>();
        Date startTime = null;
        Date endTime = null;

        if (monthType == null) {
            monthType = 0;
        }

        switch (monthType) {
            case 0:
                // 本月
                Calendar currentCalendar = Calendar.getInstance();
                currentCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startTime = DateUtils.getStartTimeByDate(currentCalendar.getTime());
                endTime = DateUtils.getEndTimeByDate(new Date());
                break;

            case 1:
                // 上一个月
                Calendar lastMonthCalendar = Calendar.getInstance();
                lastMonthCalendar.add(Calendar.MONTH, -1);
                lastMonthCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startTime = DateUtils.getStartTimeByDate(lastMonthCalendar.getTime());

                Calendar lastMonthEndCalendar = Calendar.getInstance();
                lastMonthEndCalendar.add(Calendar.MONTH, -1);
                lastMonthEndCalendar.set(Calendar.DAY_OF_MONTH, lastMonthEndCalendar.getActualMaximum(Calendar.DAY_OF_MONTH));
                endTime = DateUtils.getEndTimeByDate(lastMonthEndCalendar.getTime());
                break;

            default:
                // 默认返回本月
                Calendar defaultCalendar = Calendar.getInstance();
                defaultCalendar.set(Calendar.DAY_OF_MONTH, 1);
                startTime = DateUtils.getStartTimeByDate(defaultCalendar.getTime());
                endTime = DateUtils.getEndTimeByDate(new Date());
                break;
        }

        map.put("startTime", startTime);
        map.put("endTime", endTime);

        return map;
    }

    public static Long getTodayEndTime() {
        LocalDateTime now = LocalDateTime.now();
        // 计算今天结束的时间，这里假设“今天结束”是指今天的23:59:59
        LocalDateTime endOfDay = now.toLocalDate().atTime(23, 59, 50);
        // 计算相差的秒数
        long secondsUntilEndOfDay = ChronoUnit.SECONDS.between(now, endOfDay);
        if (secondsUntilEndOfDay <= 0) {
            return 1l;
        }
        return secondsUntilEndOfDay;
    }

    /**
     * 获取从现在到本周结束的秒数
     *
     * @return 从现在到本周结束的秒数
     */
    public static Long getWeekEndTime() {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Date> weekDate = getWeekDate50();
        Date endDate = weekDate.get("endDate");

        LocalDateTime endOfWeek = endDate.toInstant()
                .atZone(ZoneId.systemDefault()) // 使用系统默认时区
                .toLocalDateTime();
        long secondsUntilEndOfWeek = ChronoUnit.SECONDS.between(now, endOfWeek);
        return secondsUntilEndOfWeek > 0 ? secondsUntilEndOfWeek : 1L;
    }

    /**
     * 获取从现在到本月结束的秒数
     *
     * @return 从现在到本月结束的秒数
     */
    public static Long getMonthEndTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfMonth = now.toLocalDate().with(java.time.temporal.TemporalAdjusters.lastDayOfMonth()).atTime(23, 59, 50);
        long secondsUntilEndOfMonth = ChronoUnit.SECONDS.between(now, endOfMonth);
        return secondsUntilEndOfMonth > 0 ? secondsUntilEndOfMonth : 1L;
    }

    /**
     * 根据传入的时间类型返回相应的时间范围
     *
     * @param timeType 时间类型(1 每日, 2 永久 3 每周 4 每月)
     * @return 包含时间范围的 HashMap 对象
     */
    public static Map<String, Date> getTimeRangeByType(int timeType) {
        Map<String, Date> timeRange = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        switch (timeType) {
            case 1: // 每日
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                timeRange.put("startTime", calendar.getTime());
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                timeRange.put("endTime", calendar.getTime());
                break;
            case 2: // 永久
                timeRange.put("startTime", null);
                timeRange.put("endTime", null);
                break;
            case 3: // 每周
                // 设置为本周的星期一
                Map<String, Date> weekDate = getWeekDate();
                timeRange.put("startTime", weekDate.get("startDate"));
                timeRange.put("endTime", weekDate.get("endDate"));
                break;
            case 4: // 每月
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                timeRange.put("startTime", calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.SECOND, -1);
                timeRange.put("endTime", calendar.getTime());
                break;
            case 5: // 首次
                timeRange.put("startTime", null);
                timeRange.put("endTime", null);
                break;
            case 6: // 二次
                timeRange.put("startTime", null);
                timeRange.put("endTime", null);
                break;
            case 7: // 三次
                timeRange.put("startTime", null);
                timeRange.put("endTime", null);
                break;
            default:
                throw new IllegalArgumentException("Invalid timeType: " + timeType);
        }

        return timeRange;
    }

    public static Map<String, Date> getWeekDate() {
        Map<String, Date> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
// 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == 1) {
            dayWeek = 8;
        }

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);

        cal.add(Calendar.DATE, 4 + cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);
        SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期格式
        try {
            Date startDate = ss.parse(weekBegin + " 00:00:00"); //
//        Date endDate = DateUtils.formatDate(weekEnd + " 23:59:59", DateUtils.YYYY_MM_DD_HH_MM_SS);
            Date endDate = ss.parse(weekEnd + " 23:59:59"); //
            map.put("startDate", startDate);
            map.put("endDate", endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    public static Map<String, Date> getWeekDate50() {
        Map<String, Date> map = new HashMap();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cal = Calendar.getInstance();
// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
// 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayWeek == 1) {
            dayWeek = 8;
        }

        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - dayWeek);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        Date mondayDate = cal.getTime();
        String weekBegin = sdf.format(mondayDate);

        cal.add(Calendar.DATE, 4 + cal.getFirstDayOfWeek());
        Date sundayDate = cal.getTime();
        String weekEnd = sdf.format(sundayDate);
        SimpleDateFormat ss = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // 日期格式
        try {
            Date startDate = ss.parse(weekBegin + " 00:00:00"); //
//        Date endDate = DateUtils.formatDate(weekEnd + " 23:59:59", DateUtils.YYYY_MM_DD_HH_MM_SS);
            Date endDate = ss.parse(weekEnd + " 23:59:50"); //
            map.put("startDate", startDate);
            map.put("endDate", endDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return map;
    }

    /**
     * 今天过了多少小时
     *
     * @return
     */
    public static Long getTodayPassedHour() {
        long halfHoursPassed = calculateHalfHoursPassedToday();
        System.out.println("今天已经过去了 " + halfHoursPassed + " 个半个小时。");
        return halfHoursPassed;
    }

    private static long calculateHalfHoursPassedToday() {
        // 获取当前时间
        long currentTimeMillis = System.currentTimeMillis();

        // 创建一个 Calendar 对象，设置为今天的开始时间（00:00）
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        // 获取当天开始时间的毫秒数
        long startOfTodayMillis = calendar.getTimeInMillis();

        // 计算从今天开始到现在的时间间隔
        long passedMillis = currentTimeMillis - startOfTodayMillis;

        // 计算过去的半个小时数 (1个半小时 = 30分钟 = 30 * 60 * 1000毫秒)
        long halfHoursPassed = passedMillis / (30 * 60 * 1000); // 计算过去了多少个半个小时

        return halfHoursPassed;

    }

    //"0 日排行 1 周排行 2 月排行")
    public static Long changeRedisExpireTime(Integer type) {
        Long expireTime = 1l;
        switch (type) {
            case 1: // 日排行
                expireTime = DateUtils.getTodayEndTime();
                break;
            case 2: // 周排行
                expireTime = DateUtils.getWeekEndTime();
                break;
            case 3: // 月排行
                // 设置为本周的星期一
                expireTime = DateUtils.getMonthEndTime();
                break;
            default:
                return 1l;
        }
        return expireTime;
    }

    /**
     * 获取从现在到本月结束的秒数
     *
     * @return 从现在到本月结束的秒数
     */
    public static Date getMonthStartTimeByDate(Date date) {
        // 当月第一天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为指定日期
        // 设置日期为当月的第一天
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        // 从Calendar对象获取Date实例
        Date firstDayOfMonth = calendar.getTime();
        return DateUtils.getStartTimeByDate(firstDayOfMonth);

    }

    /**
     * 获取从现在到本月结束的秒数
     *
     * @return 从现在到本月结束的秒数
     */
    public static Date getMonthEndTimeByDate(Date date) {
        // 当月第一天
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date); // 设置为指定日期
        // 设置日期为当月最后一天
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH)); // 设置为当月最后一天

        // 从Calendar对象获取Date实例
        Date endDayOfMonth = calendar.getTime();
        return DateUtils.getEndTimeByDate(endDayOfMonth);

    }

    /**
     * 获取从当前传入时间 的当月开始/结束时间
     *
     * @return 本月开始/结束的时间
     */
    public static String[] getMonthStartAndEnd(String dayStr) {
        // 定义日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 将 dayStr 转换为 LocalDate
        LocalDate date = LocalDate.parse(dayStr, formatter);

        // 获取月份的开始时间和结束时间
        LocalDate startOfMonth = date.withDayOfMonth(1); // 月份开始时间
        LocalDate endOfMonth = date.withDayOfMonth(date.lengthOfMonth()); // 月份结束时间

        // 格式化为字符串并返回
        return new String[]{
                startOfMonth.format(formatter), // 格式化为 yyyy-MM-dd
                endOfMonth.format(formatter)    // 格式化为 yyyy-MM-dd
        };
    }

    /**
     * 统计接口常见的月份维度：可为 yyyy-MM（月汇总）或 yyyy-MM-dd（取该日期所在自然月的首尾）。
     */
    public static String[] getMonthStartAndEndForStatRow(String dayStr) {
        if (dayStr == null || dayStr.trim().isEmpty()) {
            return new String[]{"", ""};
        }
        String s = dayStr.trim();
        String yyyyMmDd = s.length() >= 10 ? s.substring(0, 10) : s + "-01";
        return getMonthStartAndEnd(yyyyMmDd);
    }

    /**
     * 获取从当前传入时间 的当月开始/结束时间
     *
     * @return 本月开始/结束的时间
     */
    public static String[] getMonthStartAndEnd(LocalDate dayStr) {
        // 定义日期格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        // 获取月份的开始时间和结束时间
        LocalDate startOfMonth = dayStr.withDayOfMonth(1); // 月份开始时间
        LocalDate endOfMonth = dayStr.withDayOfMonth(dayStr.lengthOfMonth()); // 月份结束时间

        // 格式化为字符串并返回
        return new String[]{
                startOfMonth.format(formatter), // 格式化为 yyyy-MM-dd
                endOfMonth.format(formatter)    // 格式化为 yyyy-MM-dd
        };
    }

    public static List<String> getAllDatesOfMonth(YearMonth yearMonth) {
        List<String> dateList = new ArrayList<>();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 从该月第一天开始，遍历到最后一天
        for (int day = 1; day <= yearMonth.lengthOfMonth(); day++) {
            LocalDate date = yearMonth.atDay(day);
            dateList.add(date.format(dateFormatter));
        }
        return dateList;
    }
    /**
     * 获取统计日期
     * @param dayStr 日期字符串（格式 yyyy-MM-dd）
     * @return 满足条件的日期字符串
     */
    public static String getStatisticalDate(String dayStr) {
        LocalDate recordDate = LocalDate.parse(dayStr, FORMATTERS);
        LocalDate now = LocalDate.now();

        // 判断是否是同年同月
        if (recordDate.getYear() == now.getYear() && recordDate.getMonth() == now.getMonth()) {
            // 同月 → 返回当天
            return now.format(FORMATTERS);
        } else {
            // 非同月 → 返回该月最后一天
            YearMonth ym = YearMonth.of(recordDate.getYear(), recordDate.getMonth());
            LocalDate lastDay = ym.atEndOfMonth();
            return lastDay.format(FORMATTERS);
        }
    }
    public static String getMonthStatisticalDate(String yearMonthStr) {
        // 解析 "yyyy-MM"
        YearMonth inputMonth = YearMonth.parse(yearMonthStr, MONTH_FORMATTER);
        YearMonth currentMonth = YearMonth.now();

        LocalDate resultDate;
        if (inputMonth.equals(currentMonth)) {
            // 当前月 -> 昨天
            resultDate = LocalDate.now().minusDays(1);
        } else {
            // 非当前月 -> 月底
            resultDate = inputMonth.atEndOfMonth();
        }

        return resultDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static void main(String[] args) {
        System.out.println(getMonthStatisticalDate("2025-10")); // 当前月时输出昨天
        System.out.println(getMonthStatisticalDate("2025-09")); // 输出2025-09-30
        String yesterday = LocalDate.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        System.out.println(yesterday);
    }
}
