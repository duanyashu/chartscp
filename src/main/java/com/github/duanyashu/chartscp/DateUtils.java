package com.github.duanyashu.chartscp;


import java.beans.PropertyEditorSupport;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 类描述：时间操作定义类
 *
 * @Author: 张代浩
 * @Date:2012-12-8 12:15:03
 * @Version 1.0
 */
public class DateUtils extends PropertyEditorSupport {

    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";

    public static final String PURE_DATE_PATTERN = "yyyyMMdd";

    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String NORM_DATETIME_MINUTE_PATTERN = "yyyy-MM-dd HH:mm";

    public static final String PURE_DATETIME_PATTERN = "yyyyMMddHHmmss";
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";

    public static final String EN_DATE_PATTERN = "yyyy/MM/dd";
    public static final String EN_DATE_MONTH_PATTERN = "yyyy/MM";
    public static final String EN_DATETIME_MINUTE_PATTERN = "yyyy/MM/dd HH:mm";
    public static final String EN_DATETIME_PATTERN = "yyyy/MM/dd HH:mm:ss";

    public static final String CN_DATE_PATTERN = "yyyy年MM月dd日";
    public static final String CN_DATE_MONTH_PATTERN = "yyyy年MM月";
    public static final String CN_DATETIME_MINUTE_PATTERN = "yyyy年MM月dd日 HH:mm";
    public static final String CN_DATETIME_PATTERN = "yyyy年MM月dd日 HH:mm:ss";

    public static final String SIMPLE_DATE_MONTH_PATTERN = "yyyy-MM";
    public static final String SIMPLE_DATE_DAY_PATTERN = "MM-dd";
    public static final String SIMPLE_DATETIME_MINUTE_PATTERN = "MM-dd HH:mm";
    public static final String SIMPLE_DATETIME_SECOND_PATTERN = "MM-dd HH:mm:ss";
    public static final String SIMPLE_TIME_MINUTE_PATTERN = "HH:mm";

    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    public static final String PURE_TIME_PATTERN = "HHmmss";

    public static final String EN_SIMPLE_DATE_DAY_PATTERN = "MM/dd";
    public static final String EN_SIMPLE_DATETIME_MINUTE_PATTERN = "MM/dd HH:mm";
    public static final String EN_SIMPLE_DATETIME_SECOND_PATTERN = "MM/dd HH:mm:ss";

    public static final String CN_SIMPLE_DATE_DAY_PATTERN = "MM月dd日";
    public static final String CN_SIMPLE_DATETIME_MINUTE_PATTERN = "MM月dd日 HH:mm";
    public static final String CN_SIMPLE_DATETIME_SECOND_PATTERN = "MM月dd日 HH:mm:ss";

    public static final String SPECIAL_SIMPLE_DATE_PATTERN = "yyyy.MM.dd";
    public static final String SPECIAL_SIMPLE_DATE_DAY_PATTERN = "MM.dd";


    private static Map<String,String> formatMap = new HashMap<>();
    static {
        //key 匹配用户输入的日期格式    value对应的格式模板
        formatMap.put("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$", "yyyy-MM-dd HH:mm:ss");
        formatMap.put("^\\d{4}/\\d{1,2}/\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$", "yyyy/MM/dd HH:mm:ss");
        formatMap.put("^\\d{4}年\\d{1,2}月\\d{1,2}日\\d{1,2}时\\d{1,2}分\\d{1,2}秒$", "yyyy年MM月dd日HH时mm分ss秒");
        formatMap.put("^\\d{4}\\d{1,2}\\d{1,2}\\d{1,2}\\d{1,2}\\d{1,2}$", "yyyyMMddHHmmss");
        formatMap.put("^\\d{4}\\d{1,2}\\d{1,2}\\d{1,2}\\d{1,2}$", "yyyyMMddHHmm");
        formatMap.put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        formatMap.put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        formatMap.put("^\\d{4}年\\d{1,2}月\\d{1,2}日$", "yyyy年MM月dd日");
        formatMap.put("^\\d{4}\\d{1,2}\\d{1,2}$", "yyyyMMdd");
        formatMap.put("^\\d{1,2}:\\d{1,2}$", "HH:mm");
    }


    // 以毫秒表示的时间
    private static final long DAY_IN_MILLIS = 24 * 3600 * 1000;
    private static final long HOUR_IN_MILLIS = 3600 * 1000;
    private static final long MINUTE_IN_MILLIS = 60 * 1000;
    private static final long SECOND_IN_MILLIS = 1000;


    // 指定模式的时间格式
    private static SimpleDateFormat getSDFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public static Calendar getCalendar() {
        return Calendar.getInstance();
    }

    public static Calendar getCalendar(long millis) {
        Calendar cal = Calendar.getInstance();
        // --------------------cal.setTimeInMillis(millis);
        cal.setTime(new Date(millis));
        return cal;
    }

    /**
     * 指定毫秒数表示的日期
     *
     * @param millis 毫秒数
     * @return 指定毫秒数表示的日期
     */
    public static Date getDate(long millis) {
        return new Date(millis);
    }


    /**
     * 格式化时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateformat(String date, String format) {
        SimpleDateFormat sformat = new SimpleDateFormat(format);
        Date _date = null;
        try {
            _date = sformat.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sformat.format(_date);
    }

    /**
     * 指定日期按指定格式显示
     *
     * @param date    指定的日期
     * @param pattern 指定的格式
     * @return 指定日期按指定格式显示
     */
    public static String formatDate(Date date, String pattern) {
        return getSDFormat(pattern).format(date);
    }

    /**
     * 日期转换为字符串
     *
     * @return 字符串
     */
    public static String getDate() {
        return getDate(NORM_DATETIME_PATTERN);
    }

    public static String getDate(String format) {
        Date date = new Date();
        if (null == date) {
            return null;
        }
        SimpleDateFormat sdf = getSDFormat(format);
        return sdf.format(date);
    }


    /**
     * 指定毫秒数的时间戳
     *
     * @param millis 毫秒数
     * @return 指定毫秒数的时间戳
     */
    public static Timestamp getTimestamp(long millis) {
        return new Timestamp(millis);
    }

    /**
     * 以字符形式表示的时间戳
     *
     * @param time 毫秒数
     * @return 以字符形式表示的时间戳
     */
    public static Timestamp getTimestamp(String time) {
        return new Timestamp(Long.parseLong(time));
    }

    /**
     * 系统当前的时间戳
     *
     * @return 系统当前的时间戳
     */
    public static Timestamp getTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }


    /**
     * 指定日期的时间戳
     *
     * @param date 指定日期
     * @return 指定日期的时间戳
     */
    public static Timestamp getTimestamp(Date date) {
        return new Timestamp(date.getTime());
    }

    /**
     * 指定日历的时间戳
     *
     * @param cal 指定日历
     * @return 指定日历的时间戳
     */
    public static Timestamp getCalendarTimestamp(Calendar cal) {
        // ---------------------return new Timestamp(cal.getTimeInMillis());
        return new Timestamp(cal.getTime().getTime());
    }

    public static Timestamp gettimestamp() {
        Date dt = new Date();
        DateFormat df = new SimpleDateFormat(NORM_DATETIME_PATTERN);
        String nowTime = df.format(dt);
        Timestamp buydate = Timestamp.valueOf(nowTime);
        return buydate;
    }



    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的日期
     * @throws ParseException
     */
    public static Calendar parseCalendar(String src, String pattern) throws ParseException {
        Date date = parseDate(src, pattern);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * 根据指定的格式将字符串转换成Date 如输入：2003-11-19 11:20:20将按照这个转成时间
     *
     * @param src     将要转换的原始字符窜
     * @param pattern 转换的匹配格式
     * @return 如果转换成功则返回转换后的日期
     * @throws ParseException
     */
    public static Date parseDate(String src, String pattern) throws ParseException {
        return getSDFormat(pattern).parse(src);

    }
    // ////////////////////////////////////////////////////////////////////////////
    // dateDiff
    // 计算两个日期之间的差值
    // ////////////////////////////////////////////////////////////////////////////


    public static int dateDiffCalender(int flag, Date calSrc, Date calDes) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(calSrc);
        c2.setTime(calDes);
        return  dateDiffCalender(flag,c1,c2);
    }
    /**
     * 计算两个时间之间的差值，根据标志的不同而不同
     *
     * @param flag   计算标志，表示按照年/月/日/时/分/秒等计算
     * @param calSrc 减数
     * @param calDes 被减数
     * @return 两个日期之间的差值
     */
    public static int dateDiffCalender(int flag, Calendar calSrc, Calendar calDes) {

        long millisDiff = calSrc.getTimeInMillis() - calDes.getTimeInMillis();

        if (flag == Calendar.YEAR) {
            return (calSrc.get(Calendar.YEAR) - calDes.get(Calendar.YEAR));
        }

        if (flag == Calendar.MONTH) {
            int year1 = calSrc.get(Calendar.YEAR);
            int year2 = calDes.get(Calendar.YEAR);
            int month1 = calSrc.get(Calendar.MONTH);
            int month2 = calDes.get(Calendar.MONTH);
            int day1 = calSrc.get(Calendar.DAY_OF_MONTH);
            int day2 = calDes.get(Calendar.DAY_OF_MONTH);
            // 获取年的差值 
            int yearInterval = year1 - year2;
            // 如果 d1的 月-日 小于 d2的 月-日 那么 yearInterval-- 这样就得到了相差的年数
            if (month1 < month2 || month1 == month2 && day1 < day2) {
                yearInterval--;
            }
            // 获取月数差值
            int monthInterval = (month1 + 12) - month2;
            if (day1 < day2) {
                monthInterval--;
            }
            monthInterval %= 12;
            int monthsDiff = Math.abs(yearInterval * 12 + monthInterval);
            return monthsDiff;
        }

        if (flag == Calendar.DATE) {
            return (int) (millisDiff / DAY_IN_MILLIS);
        }

        if (flag == Calendar.HOUR) {
            return (int) (millisDiff / HOUR_IN_MILLIS);
        }

        if (flag == Calendar.MINUTE) {
            return (int) (millisDiff / MINUTE_IN_MILLIS);
        }

        if (flag == Calendar.SECOND) {
            return (int) (millisDiff / SECOND_IN_MILLIS);
        }

        return 0;
    }

    /**
     * 字符串转日历类
     * @param arg0
     * @return
     */
    public static  Calendar strConvertCalendar(String arg0) {
        Set<Map.Entry<String,String>> entrySet = formatMap.entrySet();
        for (Map.Entry<String, String> entry : entrySet) {
            if(Pattern.matches(entry.getKey(), arg0)) {
                try {
                    return  parseCalendar(arg0, entry.getValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }



    public static int getYear() {
        Calendar calendar = getCalendar();
        return calendar.get(Calendar.YEAR);
    }

}