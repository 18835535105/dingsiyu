package com.zhidejiaoyu.common.constant;

/**
 * 勋章相关常量
 *
 * @author wuchenxi
 * @date 2019-03-17
 */
public class MedalConstant {

    /*============辉煌荣耀============*/
    /**
     * 每个勋章需要的总进度   首次完成，连续2天，连续10天。。。
     */
    public static int[] HONOUR_TOTAL_PLAN = {1, 2, 10, 20, 30};
    /**
     * 每次应该达到的目标值   首次达到20%，连续两天达到30%，连续十天达到40%。。。
     */
    public static double[] HONOUR_TARGET_PLAN = {0.2, 0.3, 0.4, 0.5, 0.8};
}
