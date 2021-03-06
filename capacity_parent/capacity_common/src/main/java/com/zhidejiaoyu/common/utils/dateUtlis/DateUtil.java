package com.zhidejiaoyu.common.utils.dateUtlis;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 日期工具类
 *
 * @author Administrator
 */
public class DateUtil implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    public static final String YYYYMMDD = "yyyy-MM-dd";

    public static final String YYYYMMDDYEAR= "yyyy年MM月dd";

    public static final String SLASHYYYYMM = "yyyy/MM/dd";

    public static final String YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    private static final String YYYY = "yyyy";

    public static final String YYYYMM = "yyyy-MM";



    /**
     * 当前时间 yyyy-MM-dd HH:mm:ss
     * 字符串
     */
    public static String DateTime() {
        SimpleDateFormat s = new SimpleDateFormat(YYYYMMDDHHMMSS);
        return s.format(new Date());
    }

    public static String formatDate(Date date, String format) {
        return new SimpleDateFormat(format).format(date);
    }

    public static Date parse(String dateStr, String format) {
        try {
            return new SimpleDateFormat(format).parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param date
     * @return yyyy
     */
    public static String strDateYYYY(Date date) {
        SimpleDateFormat s = new SimpleDateFormat(YYYY);
        return s.format(date);
    }

    /**
     * 当前年份yyyy (int类型)
     */
    public static Integer DateYYYY() {
        SimpleDateFormat s = new SimpleDateFormat(YYYY);
        String dateTime = s.format(new Date());
        return Integer.parseInt(dateTime);
    }

    /**
     * 将日期格式化为 yyyy-MM-dd 格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDD(Date date) {
        if (date == null) {
            return null;
        }
        return formatDate(YYYYMMDD, date);
    }

    /**
     * 将日期格式化为 yyyy-MM-dd HH:mm:ss 格式
     *
     * @param date
     * @return
     */
    public static String formatYYYYMMDDHHMMSS(Date date) {
        if (date == null) {
            return null;
        }
        return formatDate(YYYYMMDDHHMMSS, date);
    }

    /**
     * 将时间戳转化为没有毫秒值得时间
     *
     * @param date
     * @return
     */
    public static Date parseYYYYMMDDHHMMSS(Date date) {
        String dateStr = formatYYYYMMDDHHMMSS(date);
        if (dateStr != null) {
            try {
                return new SimpleDateFormat(YYYYMMDDHHMMSS).parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 将日期字符串格式化为 YYYYMMDDHHMMSS 格式
     *
     * @param dateStr
     * @return dateStr is empty -> return null
     */
    public static Date parseYYYYMMDDHHMMSS(String dateStr) {
        if (!StringUtils.isEmpty(dateStr)) {
            try {
                return new SimpleDateFormat(YYYYMMDDHHMMSS).parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private static String formatDate(String pattern, Date date) {
        return new SimpleDateFormat(pattern).format(date);
    }

    /**
     * 设置日期时间格式
     *
     * @param date 日期时间
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String DateTime(Date date) {
        SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return s.format(date);
    }

    /**
     * 输入毫秒数，转化为日期/时间，用calendar方法；
     *
     * @param time3
     * @return
     */
    public static String timeToDate(Long time3) {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTimeInMillis(time3);
        // int year = calendar2.get(Calendar.YEAR);
        // int month = calendar2.get(Calendar.MONTH);
        // int day = calendar2.get(Calendar.DAY_OF_MONTH);
        int hour = calendar2.get(Calendar.HOUR_OF_DAY);//24小时制
//      int hour = calendar2.get(Calendar.HOUR);//12小时制
        int minute = calendar2.get(Calendar.MINUTE);
        int second = calendar2.get(Calendar.SECOND);

        //System.out.println(year + "年" + (month + 1) + "月" + day + "日"
        //		+ hour + "时" + minute + "分" + second + "秒");

        return hour + ":" + minute + ":" + second;
    }

    public static ArrayList<String> stringToList(String s, String seperator) {
        ArrayList<String> result = new ArrayList<>();
        s = s.trim();
        int i = s.indexOf(seperator);
        while (i >= 0) {
            result.add(s.substring(0, i));
            s = s.substring(i + seperator.length()).trim();
            i = s.indexOf(seperator);
        }
        if (!s.equals("")) {
            result.add(s);
        }
        return result;
    }

    /**
     * 获取指定月份的上一月
     *
     * @param date 指定的时间 格式mm/dd
     * @return LastMONTH
     */

    public static String getlastMONTHMMDD(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMdd");

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            System.out.println("字符串转日期出现错误");
        }
        cal.add(Calendar.MONTH, -1);
        String LastMONTH = sdf.format(cal.getTime());
        return LastMONTH;
    }


    /**
     * 获取指定月份的上一月
     *
     * @param date 指定的时间 格式yyyy/mm
     * @return LastMONTH
     */

    public static String getLastMONTH(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");

        Calendar cal = Calendar.getInstance();
        try {
            cal.setTime(sdf.parse(date));
        } catch (ParseException e) {
            System.out.println("字符串转日期出现错误");
        }
        cal.add(Calendar.MONTH, -1);
        String LastMONTH = sdf.format(cal.getTime());
        return LastMONTH;
    }


    /**
     * 获取指定日期月份的第一天
     *
     * @param date   指定的日期
     * @param format
     * @return
     */
    public static String getCurrentMonthFirstDay(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        String firstDay = new SimpleDateFormat(format).format(calendar.getTime());
        return firstDay;
    }

    /**
     * 获取当前月份的第一天
     *
     * @param format
     * @return
     */
    public static String getCurrentMonthFirstDay(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        String firstDay = new SimpleDateFormat(format).format(date);
        return firstDay;
    }

    /**
     * 获取当前月份的最后一天
     *
     * @param format
     * @return
     */
    public static String getCurrentMonthLastDay(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        Date date = calendar.getTime();
        String lastDay = new SimpleDateFormat(format).format(date);
        return lastDay;
    }

    /**
     * 获取指定日期月份的最后一天
     *
     * @param date   指定的日期
     * @param format
     * @return
     */
    public static String getCurrentMonthLastDay(Date date, String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        String lastDay = new SimpleDateFormat(format).format(calendar.getTime());
        return lastDay;
    }

    /**
     * 获取上个月的第一天
     *
     * @param format
     * @return
     */
    public static String getBeforeMonthFirstDay(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        String firstDay = new SimpleDateFormat(format).format(date);
        return firstDay;
    }

    /**
     * 获取上个月的最后一天
     *
     * @param format
     * @return
     */
    public static String getBeforeMonthLastDay(String format) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        Date date = calendar.getTime();
        String lastDay = new SimpleDateFormat(format).format(date);
        return lastDay;
    }

    /**
     * 获取前一季度第一天
     *
     * @param format
     * @return
     */
    public static String getPreviousQuarterFirstDay(String format) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;//获取当前月份并转换成0-12
        double quarter = month / 3.00;//当前季度1-4
        if (0 < quarter && quarter <= 1) {//当前是第一季度，则将日历设置成上一年的第四季度的第一个月
            calendar.add(Calendar.YEAR, -1);
            calendar.set(Calendar.MONTH, 9);
        } else if (1 < quarter && quarter <= 2) {//当前是第二季度，则将日历设置成当年的第一季度的第一个月
            calendar.set(Calendar.MONTH, 0);
        } else if (2 < quarter && quarter <= 3) {//当前是第三季度，则将日历设置成当年的第二季度的第一个月
            calendar.set(Calendar.MONTH, 3);
        } else if (3 < quarter && quarter <= 4) {//当前是第四季度，则将日历设置成当年的第三季度的第一个月
            calendar.set(Calendar.MONTH, 6);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date date = calendar.getTime();
        String firstDay = new SimpleDateFormat(format).format(date);
        return firstDay;
    }

    /**
     * 获取前一季度最后一天
     *
     * @param format
     * @return
     */
    public static String getPreviousQuarterLastDay(String format) {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH) + 1;//获取当前月份并转换成0-12
        double quarter = month / 3.00;//当前季度1-4
        if (0 < quarter && quarter <= 1) {//当前是第一季度，则将日历设置成上一年的第四季度的最后一个月
            calendar.add(Calendar.YEAR, 0);
            calendar.set(Calendar.MONTH, 0);
        } else if (1 < quarter && quarter <= 2) {//当前是第二季度，则将日历设置成当年的第一季度的最后一个月
            calendar.set(Calendar.MONTH, 3);
        } else if (2 < quarter && quarter <= 3) {//当前是第三季度，则将日历设置成当年的第二季度的最后一个月
            calendar.set(Calendar.MONTH, 6);
        } else if (3 < quarter && quarter <= 4) {//当前是第四季度，则将日历设置成当年的第三季度的最后一个月
            calendar.set(Calendar.MONTH, 9);
        }
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        Date date = calendar.getTime();
        String lastDay = new SimpleDateFormat(format).format(date);
        return lastDay;
    }

    /**
     * 获取某一季度指定日期
     *
     * @param date
     * @param day
     * @return
     */
    public static Date getTheSpecifiedDate(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 按指定长度解析报文
     *
     * @param msg
     * @return
     */
    public static List<List<String>> parseMsg(String msg, int len) {
        List<List<String>> lists = new ArrayList<>();
        if (msg.startsWith("Y,")) {
            String[] strings = msg.split(",");
            for (int i = 0; i < (strings.length - 1) / len; i++) {
                List<String> list = new ArrayList<>();
                for (int j = 0; j < len; j++) {
//						strings2[i][j]=strings[i*len+j+1];
                    list.add(strings[i * len + j + 1]);

                }
                lists.add(list);
            }

        } else {
            return null;
        }
        return lists;
    }

    /**
     * 将当前系统时间按指定格式返回
     *
     * @param format
     * @return
     */
    public static String getCurrentDay(String format) {
        Date date = new Date(System.currentTimeMillis());
        String currdate = new SimpleDateFormat(format).format(date);
        return currdate;
    }

    /**
     * 获取当前日期测最大时间
     *
     * @param date
     * @return
     */
    public static Date maxTime(Date date) {
        // 获取本地标准时间（本地默认的时区时间）
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * 获取某日最小时间
     *
     * @param date
     * @return
     */
    public static Date minTime(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()), ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * hours 小时前的时间
     *
     * @param hours
     * @return
     */
    public static String beforeHoursTime(Integer hours) {
        return formatYYYYMMDDHHMMSS(new DateTime().minusHours(hours).toDate());
    }

    /**
     * 获取几周之前的日期
     *
     * @return
     */
    public static Date getBeforeWeekDate(Date date, int week) {
        return new DateTime(date).minusWeeks(week).toDate();
    }

    /**
     * 获取几天前的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getBeforeDaysDate(Date date, int days) {
        return new DateTime(date).minusDays(days).toDate();
    }

    /**
     * 获得几天后的时间
     *
     * @param date
     * @param days
     * @return
     */
    public static Date getLastDaysDate(Date date, int days) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) + days);
        return now.getTime();
    }

    /**
     * 获取几天前的日期字符串
     *
     * @param date
     * @param days
     * @return
     */
    public static String getBeforeDayDateStr(Date date, int days, String format) {
        return formatDate(getBeforeDaysDate(date, days), format);
    }

    /**
     * 获取指定日期在日期所在月位于第几周
     *
     * @param date
     * @return
     */
    public static int getWeekOfMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.WEEK_OF_MONTH);
    }

    public static int getMonth() {
        Calendar cal = Calendar.getInstance();
        return cal.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取某月最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayToMonth(Date date) {
        //获取当前月最后一天
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.set(Calendar.DAY_OF_MONTH, ca.getActualMaximum(Calendar.DAY_OF_MONTH));
        return ca.getTime();
    }

    /**
     * 将Date转换为LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime getLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return instant.atZone(zoneId).toLocalDateTime();
    }

    /**
     * 获取当前周的第几天的日期
     * 比如当前周的第一天日期
     *
     * @param day 一周中的第几天
     * @return
     */
    public static Date getDateOfWeekDay(int day) {
        if (day < 1 || day > 7) {
            throw new IllegalArgumentException("参数不能在1-7之外！");
        }
        return new DateTime().withDayOfWeek(day).toDate();
    }

    /**
     * 获取当前时间是当前周的第几天
     *
     * @return 从1开始，周一就是1，周日就是7
     */
    public static int getCurrentDayOfWeek() {
        return new DateTime().dayOfWeek().get();
    }

    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static Date getWeekStart() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        LocalDateTime monday = localDateTime.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1).withHour(0).withMinute(0).withSecond(0);
        ZonedDateTime zdt = monday.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
        return Date.from(zdt.toInstant());
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static Date getWeekEnd() {
        Date date = new Date();
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();
        LocalDateTime sunday = localDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1).withHour(23).withMinute(59).withSecond(59);
        ZonedDateTime sdt = sunday.atZone(zoneId);
        return Date.from(sdt.toInstant());
    }

    private DateUtil() {
    }

    public static void main(String[] args) {

        System.out.println(getWeekEnd());
    }
}
