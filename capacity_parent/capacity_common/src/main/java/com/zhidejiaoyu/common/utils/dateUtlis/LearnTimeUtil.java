package com.zhidejiaoyu.common.utils.dateUtlis;

import java.text.NumberFormat;

/**
 * 计算今日学习效率, 在线时长, 有效时长工具类
 */
public class LearnTimeUtil {
    /**
     * 学习效率
     *
     * @param num1 ./
     * @param num2 /.
     * @return
     */
    public static String efficiency(int num1, int num2) {
        // 创建一个数值格式化对象
        NumberFormat numberFormat = NumberFormat.getInstance();
        // 设置精确到小数点后2位
        numberFormat.setMaximumFractionDigits(0);
        String result = numberFormat.format((float) num1 / (float) num2 * 100);
        return result + "%";
    }

    /**
     * 根据秒转换00:00:00格式
     *
     * @param s 秒
     * @return
     */
    public static String validOnlineTime(Integer s) {
        if (s == null) {
            return "00:00:00";
        }
        int s1 = s % 60;
        int m = s / 60;
        int m1 = m % 60;
        int h = m / 60;
        return (h >= 10 ? (h + "") : ("0" + h)) + ":" + (m1 >= 10 ? (m1 + "") : ("0" + m1)) + ":" + (s1 >= 10 ? (s1 + "") : ("0" + s1));
    }
}
