package com.zhidejiaoyu.common.utils.study;

import java.util.HashMap;
import java.util.Map;

/**
 * 计算优先级
 *
 * @author: wuchenxi
 * @date: 2019/12/24 14:49:49
 */
public class PriorityUtil {

    /**
     * 时间基础优先级：同一学年，后一单元时间优先级=前一单元时间优先级+5
     */
    public static final int BASE_TIME_PRIORITY = 5;

    /**
     * 困难基础值=基础优先级-30
     */
    public static final int HARD_NUM = 30;

    /**
     * 各个年级对应的基础优先级
     */
    public static final Map<String, Integer> BASE_PRIORITY = new HashMap<>(16);

    /**
     * 各个年级对应的数字
     */
    public static final Map<String, Integer> GRADE_TO_NUM = new HashMap<>(16);

    static {
        intiBasePriority();
        initGradeToNum();
    }

    /**
     * 摸底测试推送课程获取优先级
     * <br>
     * 当前年级的基础优先级 + (学生所在年级序号 + 1 - 测试题所在年级序号) * n
     *
     * @param grade       学生当前所在年级
     * @param wordInGrade 测试的单词所在年级
     * @param errorCount  当前单元答错个数
     * @return
     */
    public static int getBasePriority(String grade, String wordInGrade, int errorCount) {
        if (errorCount == 1) {
            return BASE_PRIORITY.get(grade) + (GRADE_TO_NUM.get(grade) + 1 - GRADE_TO_NUM.get(wordInGrade)) * 197;
        }
        if (errorCount == 2) {
            return BASE_PRIORITY.get(grade) + (GRADE_TO_NUM.get(grade) + 1 - GRADE_TO_NUM.get(wordInGrade)) * 200;
        }
        if (errorCount == 3) {
            return BASE_PRIORITY.get(grade) + (GRADE_TO_NUM.get(grade) + 1 - GRADE_TO_NUM.get(wordInGrade)) * 203;
        }
        return BASE_PRIORITY.get(grade);
    }

    private static void initGradeToNum() {
        GRADE_TO_NUM.put("一年级", 1);
        GRADE_TO_NUM.put("二年级", 2);
        GRADE_TO_NUM.put("三年级", 3);
        GRADE_TO_NUM.put("四年级", 4);
        GRADE_TO_NUM.put("五年级", 5);
        GRADE_TO_NUM.put("六年级", 6);

        GRADE_TO_NUM.put("七年级", 7);
        GRADE_TO_NUM.put("八年级", 8);
        GRADE_TO_NUM.put("九年级", 9);

        GRADE_TO_NUM.put("高一", 10);
        GRADE_TO_NUM.put("高二", 11);
        GRADE_TO_NUM.put("高三", 12);
    }

    private static void intiBasePriority() {
        BASE_PRIORITY.put("一年级", 1000);
        BASE_PRIORITY.put("二年级", 1200);
        BASE_PRIORITY.put("三年级", 1400);
        BASE_PRIORITY.put("四年级", 1600);
        BASE_PRIORITY.put("五年级", 1800);
        BASE_PRIORITY.put("六年级", 2000);

        BASE_PRIORITY.put("七年级", 2200);
        BASE_PRIORITY.put("八年级", 2400);
        BASE_PRIORITY.put("九年级", 2600);

        BASE_PRIORITY.put("高一", 2800);
        BASE_PRIORITY.put("高二", 3000);
        BASE_PRIORITY.put("高三", 3200);
    }
}
