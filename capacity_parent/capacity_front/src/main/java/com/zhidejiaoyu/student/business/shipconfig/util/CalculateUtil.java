package com.zhidejiaoyu.student.business.shipconfig.util;

/**
 * 计算规则工具
 *
 * @author: wuchenxi
 * @date: 2020/3/2 10:55:55
 */
public class CalculateUtil {


    /**
     * 计算本周状态值（攻击百分比）
     *
     * @param baseValue      基础值
     * @param learnWordCount 最近7天学习单词数
     * @param <T>
     * @return
     */
    public static <T> String getWeekState(T baseValue, Integer learnWordCount) {
        return String.valueOf((Double) baseValue * (0.2 + learnWordCount / 30));
    }
}
