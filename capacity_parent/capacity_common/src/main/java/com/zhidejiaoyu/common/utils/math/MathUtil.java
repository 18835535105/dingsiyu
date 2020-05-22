package com.zhidejiaoyu.common.utils.math;

import java.util.Random;

/**
 * 数学计算相关工具类
 *
 * @author wuchenxi
 * @date 2018-12-14
 */
public class MathUtil {

    /**
     * 获取指定范围内的随机数 [begin, end]
     *
     * @param begin 随机数最小值
     * @param end   随机数最大值
     * @return
     */
    public static int getRandom(int begin, int end) {
        Random random = new Random();
        return random.nextInt(end) % (end - begin + 1) + begin;
    }

    /**
     * 获取指定范围内的随机数 [begin, end]
     *
     * @param begin 随机数最小值
     * @param end   随机数最大值
     * @return
     */
    public static int getRandom(long begin, long end) {
        return getRandom((int) begin, (int) end);
    }

    /**
     * 获取指定范围内的随机数 (begin, end]
     *
     * @param begin 随机数最小值
     * @param end   随机数最大值
     * @return
     */
    public static int getNotEqBeginRandom(int begin, int end) {
        return getRandom(begin + 1, end);
    }


    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(MathUtil.getRandom(2, 3));
//        }
        for (int i = 0; i < 10; i++) {
            System.out.println(MathUtil.getRandom(8, 10));
        }

    }

}
