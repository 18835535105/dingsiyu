package com.zhidejiaoyu.common.utils.dateUtlis;

import java.util.Date;

/**
 * 过去的某一时间,计算距离当前的时间
 *
 * @author qizhentao
 * @version 1.0
 */
public class CalculateTimeUtil {

    /**
     * 过去的某一时间,计算距离当前的时间
     *
     * @param time 过去的时间格式: yyyy-MM-dd HH:mm:ss
     */
    public static String calculateTime(String time) {
        // 获取当前时间的毫秒数
        long nowTime = System.currentTimeMillis();

        // 指定时间
        Date setTime = DateUtil.parseYYYYMMDDHHMMSS(time);

        if (setTime == null) {
            return null;
        }

        // 获取指定时间的毫秒数
        long reset = setTime.getTime();
        long dateDiff = nowTime - reset;

        if (dateDiff < 0) {
            return "输入的时间不对";
        }

        // 秒
        long dateTemp1 = dateDiff / 1000;
        // 分钟
        long dateTemp2 = dateTemp1 / 60;
        // 小时
        long dateTemp3 = dateTemp2 / 60;
        // 天数
        long dateTemp4 = dateTemp3 / 24;
        // 月数
        long dateTemp5 = dateTemp4 / 30;
        // 年数
        long dateTemp6 = dateTemp5 / 12;

        if (dateTemp6 > 0) {
            return dateTemp6 + "年前";

        }
        if (dateTemp5 > 0) {
            return dateTemp5 + "个月前";

        }
        if (dateTemp4 > 0) {
            return dateTemp4 + "天前";

        }
        if (dateTemp3 > 0) {
            return dateTemp3 + "小时前";

        }
        if (dateTemp2 > 0) {
            return dateTemp2 + "分钟前";

        }
        if (dateTemp1 > 0) {
            return "刚刚";
        }
        return null;
    }

}
