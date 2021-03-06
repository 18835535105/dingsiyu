package com.zhidejiaoyu.common.utils.dateUtlis;

/**
 * 时间工具类
 */
public class TimeUtil {

    /**
     * 一小时的秒数
     */
    private static final int HOUR_SECOND = 60 * 60;

    /**
     * 一分钟的秒数
     */
    private static final int MINUTE_SECOND = 60;

    /**
     * 根据秒数获取时间串
     * @param second (eg: 100s)
     * @return (eg: 00:01:40)
     */
    public static String getTimeStrBySecond(Integer second) {
        if (second==null || second <= 0) {

            return "00小时00分00秒";
        }

        StringBuilder sb = new StringBuilder();
        int hours = second / HOUR_SECOND;
        if (hours > 0) {

            second -= hours * HOUR_SECOND;
        }

        int minutes = second / MINUTE_SECOND;
        if (minutes > 0) {

            second -= minutes * MINUTE_SECOND;
        }

        return (hours >= 10 ? (hours + "")
                : ("0" + hours) + "小时" + (minutes >= 10 ? (minutes + "") : ("0" + minutes)) + "分"
                        + (second >= 10 ? (second + "") : ("0" + second))+"秒");
    }
}
