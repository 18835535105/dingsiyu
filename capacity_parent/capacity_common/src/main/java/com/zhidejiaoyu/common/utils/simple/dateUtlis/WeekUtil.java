package com.zhidejiaoyu.common.utils.simple.dateUtlis;

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
		Calendar c = new GregorianCalendar();
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.setMinimalDaysInFirstWeek(7);
		c.setTime(date);

		return c.get(Calendar.WEEK_OF_YEAR);
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
		return SimpleDateUtil.formatYYYYMMDD(d);
	}

	public static void main(String[] args) {



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
