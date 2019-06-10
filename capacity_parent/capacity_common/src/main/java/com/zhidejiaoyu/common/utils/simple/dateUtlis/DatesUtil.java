package com.zhidejiaoyu.common.utils.simple.dateUtlis;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatesUtil {

    /**
     * 日期格式化
     * @param date
     * @return
     */
    public static Date getCurrentDate(Date date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String format = df.format(date);
        Date parse=null;
        try {
            parse = df.parse(format);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    /**
     * 将字符串转化成Date
     * @param date
     * @return
     */
    public static Date getname(String date){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date parse = null;
        try {
             parse=df.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    /**
     * 获取日期为星期几
     * @param date
     * @return
     */
    public static int weekForDay(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 获取星期一是几号
     * @param date
     * @return
     */
    public static Date getOnMonday(Date date){
        int week = weekForDay(date);
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        switch (week){
            case 1 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)-6);
                break;
            case 2 :
                break;
            case 3 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)-1);
                break;
            case 4 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)-2);
                break;
            case 5 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)-3);
                break;
            case 6 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)-4);
                break;
            case 7 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)-5);
                break;
        }
        return now.getTime();
    }
    /**
     * 获取第一次登入星期一是几号
     * @param date
     * @return
     */
    public static Date getOnSunday(Date date){
        int week = weekForDay(date);
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        switch (week){
            case 1 :
                break;
            case 2 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)+6);
                break;
            case 3 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)+5);
                break;
            case 4 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)+4);
                break;
            case 5 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)+3);
                break;
            case 6 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)+2);
                break;
            case 7 :
                now.set(Calendar.DATE,now.get(Calendar.DATE)+1);
                break;
        }
        return now.getTime();
    }

    /**
     * 获取指定日期所在周周一与现在所在周周日差距的周数
     * @return
     */
    public static int getTimeGapDays(Date date){
        Date onMonday = getOnMonday(date);
        Date onSunday = getOnSunday(getCurrentDate(new Date()));
        //获取两方差距天数
        int days=(int)((onSunday.getTime()-onMonday.getTime())/(1000*60*60*24)+1);
        return (int)(days/7+1);
    }

    /**
     * 获取几周之前的日期
     * @return
     */
    public static Date getBeforeWeekDate(Date date , int week){
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        int days=((week-1)*7);
        now.set(Calendar.DATE,now.get(Calendar.DATE)-days);
        return now.getTime();
    }




}
