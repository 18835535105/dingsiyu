package com.zhidejiaoyu.student.constant;

/**
 * 宠物提示语 MP3 名称常量类
 *  数组顺序 "李糖心", "威士顿", "大明白", "无名"
 * @author wuchenxi
 * @date 2018/7/25
 */
public class PetMP3Constant {

    /**
     * 当选择宠物角色时，发出各个角色对应的座右铭录音
     */
   // public static final int[] CHOOSE_PET = {1, 2, 3, 4};
    /**
     * 进入‘选填信息’页
     */
   // public static final int[] CHOOSE_INFO = {5, 6, 7, 8};
    /**
     * 在正式进入学习前的‘游戏测试’开始页
     */
   // public static final int[] BEGIN_GAME_TEST = {9, 10, 11, 12};
    /**
     * 点击‘跳过游戏测试'
     */
   // public static final int[] JUMP_GAME_TEST = {13, 14, 15, 16};
    /**
     * 当某个课程某个模块下的单词复习量达到一定值，要强制去进行复习测试
     */
    public static final int[] REVIEW_TEST = {17, 18, 19, 20};
    /**
     * 阶段测试强制复习提醒页
     */
    public static final int[] STAGE_TEST = {21, 22, 23, 24};
    /**
     * 摸底测试，成绩小于80分
     */
    public static final int[] LEVEL_TEST_LESS_EIGHTY = {25, 26, 27, 28};
    /**
     * 摸底测试，成绩在80-90分（包含80）
     */
    public static final int[] LEVEL_TEST_EIGHTY_TO_NINETY = {29, 30, 31, 32};
    /**
     * 摸底测试，成绩 >= 90分
     */
    public static final int[] LEVEL_TEST_GREATER_NINETY = {33, 34, 35, 36};
    /**
     * 单元闯关测试，成绩 < 80 分
     */
    public static final int[] UNIT_TEST_LESS_EIGHTY = {37, 38, 39, 40};
    /**
     * 单元闯关测试，成绩 80 ~ 100 分
     */
    public static final int[] UNIT_TEST_EIGHTY_TO_HUNDRED = {41, 42, 43, 44};
    /**
     * 单元闯关测试，成绩 =100 分
     */
    public static final int[] UNIT_TEST_HUNDRED = {45, 46, 47, 48};
    /**
     * 阶段测试 0~80 分
     */
    public static final int[] STAGE_TEST_LESS_EIGHTY = {49, 50, 51, 52};
    /**
     * 阶段测试 80~100 分
     */
    public static final int[] STAGE_TEST_EIGHTY_TO_HUNDRED = {52, 54, 55, 56};
    /**
     * 智能复习测试(测试复习)，得分为0-80分
     */
    public static final int[] CAPACITY_REVIEW_LESS_EIGHTY = {57, 58, 59, 60};
    /**
     * 智能复习测试(测试复习)，得分在80-100分之间
     */
    public static final int[] CAPACITY_REVIEW_EIGHTY_TO_HUNDRED = {61, 62, 63, 64};
    /**
     * 已学测试+生词测试+熟词测试，得分<80时
     */
    public static final int[] TEST_CENTER_LESS_EIGHTY = {65, 66, 67, 68};
    /**
     * 已学测试+生词测试+熟词测试，得分在80--90之间
     */
    public static final int[] TEST_CENTER_EIGHTY_TO_NINETY = {69, 70, 71, 72};
    /**
     * 已学测试+生词测试+熟词测试，得分在90--100之间
     */
    public static final int[] TEST_CENTER_NINETY_TO_HUNDRED = {73, 74, 75, 76};
    /**
     * 五维测试,得分为0-80分
     */
    public static final int[] FIVE_TEST_LESS_EIGHTY = {77, 78, 79, 80};
    /**
     * 五维测试,得分为80-100分
     */
    public static final int[] FIVE_TEST_EIGHTY_TO_HUNDRED = {81, 82, 83, 84};

    /*=============================================*/
    /*================ 清学版宠物录音 ===============*/
    /*=============================================*/
    /**
     * 课程前测，课程后测 0-80分
     */
    public static final int[] COURSE_TEST_LESS_EIGHTY = {61, 58, 59, 60};
    /**
     * 课程前测，课程后测 80-100分
     */
    public static final int[] COURSE_TEST_EIGHTY_TO_HUNDRED = {53, 54, 55, 56};

    /**
     * 单元前测 第一阶段得分 <br>
     * <ul>单词默写、词组默写
     * <li>x<50：65, 66, 67, 68</li>
     * </ul>
     *
     * <ul>单词辩音、词组辩音
     * <li>x<60：65, 66, 67, 68</li>
     * </ul>
     *
     * <ul>快速单词、快速词组、快速句型、词汇考点、语法辨析
     * <li>x<80：65, 66, 67, 68</li>
     * </ul>
     */
    public static final int[] BEFORE_UNIT_FIRST_LEVEL = {65, 66, 67, 68};
    /**
     * 单元前测 第二阶段得分
     * <ul>单词默写、词组默写
     * <li>50≤x<90：69, 70, 71, 72</li>
     * </ul>
     *
     * <ul>单词辩音、词组辩音
     * <li>60≤x≤90：73, 74, 75, 76</li>
     * </ul>
     *
     * <ul>
     * 快速单词、快速词组、快速句型、词汇考点、语法辨析
     * <li>80≤x<90：69, 70, 71, 72</li>
     * </ul>
     */
    public static final int[] BEFORE_UNIT_SECOND_LEVEL = {69, 70, 71, 72};
    /**
     * 单元前测 第三阶段得分
     * <ul>单词默写、词组默写
     * <li>90≤x≤100：73, 74, 75, 76</li>
     * </ul>
     *
     * <ul>单词辩音、词组辩音
     * <li>90≤x≤100：73, 74, 75, 76</li>
     * </ul>
     *
     * <ul>
     * 快速单词、快速词组、快速句型、词汇考点、语法辨析
     * <li>90≤x≤100：73, 74, 75, 76</li>
     * </ul>
     */
    public static final int[] BEFORE_UNIT_THIRD_LEVEL = {73, 74, 75, 76};

    /**
     * 单元闯关 第一阶段得分
     * <ul>
     *     单词默写、词组默写
     *     <li>x<50分：37, 38, 39, 40</li>
     * </ul>
     *
     * <ul>
     * 单词辩音、词组辩音
     * <li>x<60分：37, 38, 39, 40</li>
     * </ul>
     *
     * <ul>
     * 快速单词、快速词组、快速句型、词汇考点、语法辨析
     * <li>x<80：37, 38, 39, 40</li>
     * </ul>
     */
    public static final int[] AFTER_UNIT_FIRST_LEVEL = {37, 38, 39, 40};
    /**
     * 单元闯关 第二阶段得分
     * <ul>
     * 单词默写、词组默写、单词辩音、词组辩音
     * <li>60≤x<100：41, 42, 43, 44</li>
     * </ul>
     *
     * <ul>
     * 快速单词、快速词组、快速句型、词汇考点、语法辨析
     * <li>80≤x<100：41, 42, 43, 44</li>
     * </ul>
     */
    public static final int[] AFTER_UNIT_SECOND_LEVEL = {41, 42, 43, 44};
    /**
     * 单元闯关 第三阶段得分
     * <ul>
     * 单词默写、词组默写、单词辩音、词组辩音
     * <li>x=100：45, 46, 47, 48</li>
     * </ul>
     *
     * <ul>
     * 快速单词、快速词组、快速句型、词汇考点、语法辨析
     * <li>x=100：45, 46, 47, 48</li>
     * </ul>
     */
    public static final int[] AFTER_UNIT_THIRD_LEVEL = {45, 46, 47, 48};

    /**
     * 能力值测试 0-80  x<80：57,78,79,80
     */
    public static final int[] WORD_TEST_LESS_EIGHTY = {57, 78, 79, 80};
    /**
     * 能力值测试 80-100分    80≤x≤100：81, 82, 83, 84
     */
    public static final int[] WORD_TEST_EIGHTY_TO_HUNDRED = {81, 82, 83, 84};
}
