package com.zhidejiaoyu.common.study;

import com.zhidejiaoyu.common.utils.math.MathUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 测试得分战胜百分比工具类
 *
 * @author wuchenxi
 * @date 2019-03-07
 */
@Slf4j
public class TestPointUtil {

    public static String getPercentage(Integer point) {
        if (point == null) {
            point = 0;
        }
        if (point >= 90) {
            return getMoreThanNinety(point);
        }
        return getLessThanNinety(point);
    }

    private static String getLessThanNinety(Integer point) {

        if (point >= 85 && point < 90) {
            return MathUtil.getNotEqBeginRandom(80, 90) + "%";
        }
        if (point >= 0 && point < 10) {
            return "1%";
        }
        if (point >= 80 && point < 85) {
            return MathUtil.getNotEqBeginRandom(75, 80) + "%";
        }
        if (point >= 70 && point < 80) {
            return MathUtil.getNotEqBeginRandom(70, 75) + "%";
        }
        if (point >= 60 && point < 70) {
            return MathUtil.getNotEqBeginRandom(65, 70) + "%";
        }
        if (point >= 50 && point < 60) {
            return MathUtil.getNotEqBeginRandom(55, 65) + "%";
        }
        if (point >= 40 && point < 50) {
            return MathUtil.getNotEqBeginRandom(45, 55) + "%";
        }
        if (point >= 30 && point < 50) {
            return MathUtil.getNotEqBeginRandom(15, 20) + "%";
        }
        if (point >= 20 && point < 30) {
            return MathUtil.getNotEqBeginRandom(10, 15) + "%";
        }
        if (point >= 10 && point < 20) {
            return MathUtil.getNotEqBeginRandom(5, 10) + "%";
        }
        return "0%";
    }

    private static String getMoreThanNinety(int point) {
        if (point == 90 || point == 91) {
            return "90%";
        }
        return (point - 1) + "%";
    }

    public static void main(String[] args) {
        int[] point = {0, 10, 20, 30, 40, 50, 60, 70, 80, 85, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100};
        for (int i : point) {
            log.info("point={},result={}", i, TestPointUtil.getPercentage(i));
        }

    }
}
