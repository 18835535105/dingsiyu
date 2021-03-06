package com.zhidejiaoyu.common.utils.study;

import com.zhidejiaoyu.common.constant.GradeNameConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 计算优先级
 *
 * @author: wuchenxi
 * @date: 2019/12/24 14:49:49
 */
@Slf4j
public class PriorityUtil {

    /**
     * 时间基础优先级：同一学年，后一单元时间优先级=前一单元时间优先级+5
     */
    public static final int BASE_TIME_PRIORITY = 5;

    /**
     * 困难基础值=基础优先级-10
     */
    public static final int HARD_NUM = 10;

    /**
     * 金币试卷基础值=困难基础值-50
     */
    public static final int GOLD_TEST_NUM = 50;

    /**
     * 各个年级对应的基础优先级
     */
    public static final Map<String, Integer> BASE_PRIORITY = new HashMap<>(16);

    /**
     * 各个年级对应的数字
     */
    public static final Map<String, Integer> GRADE_TO_NUM = new HashMap<>(16);

    static {
        initBasePriority();
        initGradeToNum();
    }

    /**
     * 摸底测试推送课程获取优先级
     * <br>
     * 当前年级的基础优先级 + (学生所在年级序号 + 1 - 测试题所在年级序号) * n
     *
     * @param grade       学生当前所在年级
     * @param wordInGrade 测试的单词所在年级
     * @param wordInLabel 测试的单元所在上下册
     * @return
     */
    public static int getBasePriority(String grade, String wordInGrade, String wordInLabel) {

        int month = new DateTime().monthOfYear().get();

        // 非当前学年，六年级下册均为2200，高三下册为3400
        // 学生不是6年级，当前测试单元为6年级下册，基础优先级为2200
        if (!Objects.equals(grade, GradeNameConstant.SIXTH_GRADE)
                && Objects.equals(wordInGrade, GradeNameConstant.SIXTH_GRADE)
                && Objects.equals(GradeNameConstant.VOLUME_2, wordInLabel)
                && month < 9) {
            return 2200;
        }
        // 学生年级不是高三，当前测试单元为高三下册单元，基础优先级为3400
        if (!Objects.equals(grade, GradeNameConstant.SENIOR_THREE)
                && Objects.equals(wordInGrade, GradeNameConstant.SENIOR_THREE)
                && Objects.equals(GradeNameConstant.VOLUME_2, wordInLabel)
                && month < 9) {
            return 3400;
        }

        return BASE_PRIORITY.get(wordInGrade) == null ? 0 : BASE_PRIORITY.get(wordInGrade);
    }

    /**
     * 获取变化优先级
     *
     * @param errorCount  错题数
     * @param grade       学生当前年级
     * @param wordInGrade 测试的单词所在年级
     * @return
     */
    public static int getErrorPriority(int errorCount, String grade, String wordInGrade) {
        switch (errorCount) {
            case 1:
                return (GRADE_TO_NUM.get(grade) - GRADE_TO_NUM.get(wordInGrade)) * 200
                        + (GRADE_TO_NUM.get(grade) + 1 - GRADE_TO_NUM.get(wordInGrade)) * -3;
            case 2:
                return (GRADE_TO_NUM.get(grade) - GRADE_TO_NUM.get(wordInGrade)) * 200;
            case 3:
                return (GRADE_TO_NUM.get(grade) - GRADE_TO_NUM.get(wordInGrade)) * 200
                        + (GRADE_TO_NUM.get(grade) + 1 - GRADE_TO_NUM.get(wordInGrade)) * 3;
            default:
                return 0;
        }
    }

    /**
     * 年级转换为对应的数字
     */
    private static void initGradeToNum() {
        GRADE_TO_NUM.put("小学", 1);
        GRADE_TO_NUM.put("一年级", 1);
        GRADE_TO_NUM.put("二年级", 2);
        GRADE_TO_NUM.put("三年级", 3);
        GRADE_TO_NUM.put("四年级", 4);
        GRADE_TO_NUM.put("五年级", 5);
        GRADE_TO_NUM.put("六年级", 6);

        GRADE_TO_NUM.put("初中", 7);
        GRADE_TO_NUM.put("七年级", 7);
        GRADE_TO_NUM.put("八年级", 8);
        GRADE_TO_NUM.put("九年级", 9);

        GRADE_TO_NUM.put("高中", 10);
        GRADE_TO_NUM.put("高一", 10);
        GRADE_TO_NUM.put("高二", 11);
        GRADE_TO_NUM.put("高三", 12);
    }

    /**
     * 获取各个年级的基础优先级值
     */
    private static void initBasePriority() {
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

    /**
     * 每天定时任务 计算学生变化率
     */
    public static int calculateRateOfChange(String studentGrade, String studyGrade) {
        return 3 * (GRADE_TO_NUM.get(studentGrade) + 1 - GRADE_TO_NUM.get(studyGrade));
    }

    /**
     * 每天定时任务 计算时间变化率
     */
    public static int calculateTimeOfChange(int number) {
        return 5 * number;
    }

    /**
     * 获取难易基础值
     *
     * @param grade
     * @param type
     * @return
     */
    public static int getEasyOrHard(String grade, int type) {
        if (type == 1) {
            return BASE_PRIORITY.get(grade);
        } else {
            return BASE_PRIORITY.get(grade) - 100;
        }

    }

    /**
     * 每学习完一个单元，更新优先级
     *
     * @param maxFinalLevel
     * @param grade         学生当前年级
     * @param currentGrade  教材年级
     */
    public static void finishUnitUpdateErrorLevel(StudentStudyPlanNew maxFinalLevel, String grade, String currentGrade) {
        Student student = (Student) HttpUtil.getHttpSession().getAttribute(UserConstant.CURRENT_STUDENT);

        String oldStudentStudyPlanNew = maxFinalLevel.toString();

        printNotFoundGradeNumLog(grade, currentGrade);

        int errorLevel = maxFinalLevel.getErrorLevel() - 5 * (GRADE_TO_NUM.get(grade) + 1 - GRADE_TO_NUM.get(currentGrade));
        maxFinalLevel.setErrorLevel(errorLevel);
        maxFinalLevel.setFinalLevel(errorLevel + maxFinalLevel.getTimeLevel() + maxFinalLevel.getBaseLevel());
        maxFinalLevel.setUpdateTime(new Date());
        log.info("学生[{} - {} - {}]学习完单元更新优先级，原优先级：[{}]， 更新后优先级：[{}]", student.getId(), student.getAccount(),
                student.getStudentName(), oldStudentStudyPlanNew, maxFinalLevel.toString());
    }

    private static void printNotFoundGradeNumLog(String grade, String currentGrade) {
        if (!GRADE_TO_NUM.containsKey(grade)) {
            log.warn("GRADE_TO_NUM不包含{}", grade);
        }
        if (!GRADE_TO_NUM.containsKey(currentGrade)) {
            log.warn("currentGrade不包含{}", currentGrade);
        }
    }
}
