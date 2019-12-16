package com.zhidejiaoyu.common.utils.dateUtlis;

import org.joda.time.DateTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 时间周工具类
 *
 * @author qizhentao
 * @version 1.0
 */
public class WeekUtil {

	 /** 获取当前时间所在年的周数
	  *
	  * @param date
	  */
	 public static int getWeekOfYear(Date date) {
	 	return new DateTime(date).getWeekOfWeekyear();
	 }

	 /** 获取当前时间所在年的最大周数
	  *
	  * @param year 年  ; 例:2018
	  */
	 public static int getMaxWeekNumOfYear(int year) {
	  Calendar c = new GregorianCalendar();
	  c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);

	  return getWeekOfYear(c.getTime());
	 }

	 /** 获取某年的第几周的开始日期
	  *
	  * @param year 年
	  * @param week 周
	  */
	 public static Date getFirstDayOfWeek(int year, int week) {
	  Calendar c = new GregorianCalendar();
	  c.set(Calendar.YEAR, year);
	  c.set(Calendar.MONTH, Calendar.JANUARY);
	  c.set(Calendar.DATE, 1);

	  Calendar cal = (GregorianCalendar) c.clone();
	  cal.add(Calendar.DATE, week * 7);

	  return getFirstDayOfWeek(cal.getTime());
	 }

	 /** 获取某年的第几周的结束日期
	  *
	  * @param year 年
	  * @param week 周
	  */
	 public static Date getLastDayOfWeek(int year, int week) {
	  Calendar c = new GregorianCalendar();
	  c.set(Calendar.YEAR, year);
	  c.set(Calendar.MONTH, Calendar.JANUARY);
	  c.set(Calendar.DATE, 1);

	  Calendar cal = (GregorianCalendar) c.clone();
	  cal.add(Calendar.DATE, week * 7);

	  return getLastDayOfWeek(cal.getTime());
	 }

	 /** 获取当前时间所在周的开始日期 */
	 public static Date getFirstDayOfWeek(Date date) {
	  Calendar c = new GregorianCalendar();
	  c.setFirstDayOfWeek(Calendar.MONDAY);
	  c.setTime(date);
	  c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
	  return c.getTime();
	 }

	 /** 获取当前时间所在周的结束日期 */
	 public static Date getLastDayOfWeek(Date date) {
	  Calendar c = new GregorianCalendar();
	  c.setFirstDayOfWeek(Calendar.MONDAY);
	  c.setTime(date);
	  c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
	  return c.getTime();
	 }

	/**
	 * 获取指定日期，所在本月一号的日期
	 * @param
	 * @return yyyy-mm-dd
	 */
	public static String getMonthOne(Date d){
		Calendar c = Calendar.getInstance();
		// 这是已知的日期
		c.setTime(d);
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		c.set(Calendar.DAY_OF_MONTH, 1);
		// 1号的日期
		d = c.getTime();
		return DateUtil.formatYYYYMMDD(d);
	}

	/**
	 * 获取指定日期所属周的周几所在的日期，例如当前时间周日所在的日期是***
	 *
	 * @param date	目标日期
	 * @param dayOfWeek 周几，周一到周日为 1 到 7
	 * @return
	 */
	public static Date getMondayDate(Date date, int dayOfWeek) {
		return new DateTime(date).withDayOfWeek(dayOfWeek).toDate();
	}

	/**
	 * 获取指定日期所在周周一与现在所在周周日差距的周数
	 *
	 * @return
	 */
	public static int getTimeGapDays(Date date) {
		Date onMonday = WeekUtil.getMondayDate(date, 1);
		Date onSunday = WeekUtil.getMondayDate(new Date(), 7);
		//获取两方差距天数
		int days = (int) ((onSunday.getTime() - onMonday.getTime()) / (1000 * 60 * 60 * 24) + 1);
		return days / 7 + 1;
	}

	public static void main(String[] args) {

		System.out.println(getWeekOfYear(new Date()));

		System.out.println(DateUtil.formatYYYYMMDDHHMMSS(getMondayDate(new Date(), 1)));
		System.out.println(DateUtil.formatYYYYMMDDHHMMSS(getMondayDate(new Date(), 7)));
		System.out.println(DateUtil.formatYYYYMMDDHHMMSS(getMondayDate(new Date(), 0)));

		System.out.println(new DateTime().getWeekyear());
	 /* int year = 2011;
	  int week = 1;

	  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  Date today = new Date();
	  Calendar c = new GregorianCalendar();
	  c.setTime(today);

	  System.out.println("current date = " + sdf.format(today));
	  System.out.println("getWeekOfYear = " + getWeekOfYear(today));
	  System.out.println("getMaxWeekNumOfYear = " + getMaxWeekNumOfYear(year));
	  System.out.println("getFirstDayOfWeek = " + sdf.format(getFirstDayOfWeek(year, week)));
	  System.out.println("getLastDayOfWeek = " + sdf.format(getLastDayOfWeek(year, week)));
	  System.out.println("getFirstDayOfWeek = " + sdf.format(getFirstDayOfWeek(today)));
	  System.out.println("getLastDayOfWeek = " + sdf.format(getLastDayOfWeek(today)));*/
	 }
}
