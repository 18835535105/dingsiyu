package com.zhidejiaoyu.common.constant;

/**
 * 测试相关奖励金币数常量类
 *
 * @author wuchenxi
 * @date 2018/8/7
 */
public class TestAwardGoldConstant {

	/**
	 * 新生 流程17 point < 80
	 */
	public static final int FLOW_TEST_EIGHT_ZERO = 2;
	/**
	 * 新生 流程17 point >= 80 && point < 90
	 */
	public static final int FLOW_TEST_EIGHTY_TO_FULL = 5;
	/**
	 * 新生 流程17 point >= 90
	 */
	public static final int FLOW_TEST_NINETY_TO_FULL = 10;

    /**
     * 单元闯关测试分数  point >= 80 && point < 100
     */
    public static final int UNIT_TEST_EIGHTY_TO_FULL = 3;

	/**
     * 单元闯关测试分数 point >= 60 && point < 70
     */
    public static final int UNIT_TEST_SIXTY_TO_SEVENTY = 1;
    /**
     * 单元闯关测试分数 point >= 70 && point < 80
     */
    public static final int UNIT_TEST_SEVENTY_TO_EIGHTY = 2;
    /**
     * 单元闯关测试分数  point >= 80 && point < 90
     */
    public static final int UNIT_TEST_EIGHTY_TO_NINETY = 3;
    /**
     * 单元闯关测试分数  point >= 90 && point < 100
     */
    public static final int UNIT_TEST_NINETY_TO_FULL = 4;
    /**
     * 单元闯关测试分数  point == 100
     */
    public static final int UNIT_TEST_FULL = 5;

    /**
     * 五维测试分数 point >= 80 && point < 90
     */
    public static final int FIVE_TEST_EIGHTY_TO_NINETY = 10;
    /**
     * 五维测试分数 point >= 90 && point < 100
     */
    public static final int FIVE_TEST_NINETY_TO_FULL = 20;
    /**
     * 生词测试，熟词测试，已学测试 分数 point >= 80 && point < 90
     */
    public static final int TEST_CENTER_EIGHTY_TO_NINETY = 2;
    /**
     * 生词测试，熟词测试，已学测试 分数 point >= 90 && point <= 100
     */
    public static final int TEST_CENTER_NINETY_TO_FULL = 5;
    /**
     * 测试复习 分数 point >= 90
     */
    public static final int REVIEW_TEST_NINETY_TO_FULL = 1;
}
