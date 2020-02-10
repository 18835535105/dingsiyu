package com.zhidejiaoyu.common.utils.grade;

import java.util.*;

/**
 * 获取年级工具类
 *
 * @author: wuchenxi
 * @date: 2019/12/19 10:58:58
 */
public class GradeUtil {

    /**
     * 通用小学年级
     */
    private static final String[] SMALL_GRADE = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级"};

    /**
     * 通用小学年级
     */
    private static final String[] MIDDLE_GRADE = {"七年级", "八年级", "九年级"};

    /**
     * 通用小学年级
     */
    private static final String[] HIGH_GRADE = {"高一", "高二", "高三"};

    /**
     * 通用全部年级
     */
    private static final String[] NORMAL_GRADE = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级", "七年级", "八年级", "九年级", "高一", "高二", "高三"};

    /**
     * 全学段年级
     */
    private static final String[] IN_THE_WHOLE_PERIOD = {"入门级", "1", "2"};

    /**
     * 年级为中学的
     */
    private static final String[] MIDDLE_SCHOOL = {"中学"};

    /**
     * 剑桥少儿年级
     */
    private static final String[] JIANQIAO = {"预备级（5-6岁）", "一级（6-8岁）", "二级（8-11岁）", "三级"};

    /**
     * 朗文国际年级，初中
     */
    private static final String[] LANGWEN = {"第一册", "第二册", "第三册", "第四册"};

    /**
     * 版本名与年级数组对应（只是当前学段的）
     */
    private static Map<String, String[]> VERSION_GRADE;

    /**
     * 版本名与年级数据嘴硬（不区分学段）
     */
    private static Map<String, String[]> ALL_PHASE_VERSION_GRADE;
    
    static {
        initVersionGrade();

        initAllPhaseVersionGrade();
    }



    /**
     * 获取当前学段小于或等于当前年级的年级集合
     *
     * @param version 版本
     * @param grade   年级
     * @return
     */
    public static List<String> smallThanCurrent(String version, String grade) {

        String[] gradeArr = VERSION_GRADE.get(version);
        if (gradeArr != null) {
            return getGradeList(grade, gradeArr);
        }

        return getGradeList(grade, VERSION_GRADE.get(grade));
    }

    /**
     * 获取小于或等于当前年级的年级集合（不区分学段），比如当前年级是8年级，获取1~8年级集合
     *
     * @param version
     * @param grade
     * @return
     */
    public static List<String> smallThanCurrentAllPhase(String version, String grade) {
        String[] gradeArr = ALL_PHASE_VERSION_GRADE.get(version);
        if (gradeArr != null) {
            return getGradeList(grade, gradeArr);
        }

        return getGradeList(grade, ALL_PHASE_VERSION_GRADE.get(grade));
    }

    private static List<String> getGradeList(String grade, String[] gradeArr) {
        List<String> gradeList = new ArrayList<>();
        for (String s : gradeArr) {
            gradeList.add(s);
            if (Objects.equals(grade, s)) {
                return gradeList;
            }
        }
        return Collections.emptyList();
    }

    private static void initAllPhaseVersionGrade() {
        ALL_PHASE_VERSION_GRADE = new HashMap<>(16);
        ALL_PHASE_VERSION_GRADE.put("朗文国际", LANGWEN);
        ALL_PHASE_VERSION_GRADE.put("剑桥少儿英语（西安交大版）", JIANQIAO);
        ALL_PHASE_VERSION_GRADE.put("新概念经典版（中学）", MIDDLE_SCHOOL);
        ALL_PHASE_VERSION_GRADE.put("剑桥英语青少版", IN_THE_WHOLE_PERIOD);

        ALL_PHASE_VERSION_GRADE.put("一年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("二年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("三年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("四年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("五年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("六年级", NORMAL_GRADE);

        ALL_PHASE_VERSION_GRADE.put("七年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("八年级", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("九年级", NORMAL_GRADE);

        ALL_PHASE_VERSION_GRADE.put("高一", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("高二", NORMAL_GRADE);
        ALL_PHASE_VERSION_GRADE.put("高三", NORMAL_GRADE);
    }

    private static void initVersionGrade() {
        VERSION_GRADE = new HashMap<>(16);
        VERSION_GRADE.put("朗文国际", LANGWEN);
        VERSION_GRADE.put("剑桥少儿英语（西安交大版）", JIANQIAO);
        VERSION_GRADE.put("新概念经典版（中学）", MIDDLE_SCHOOL);
        VERSION_GRADE.put("剑桥英语青少版", IN_THE_WHOLE_PERIOD);

        VERSION_GRADE.put("一年级", SMALL_GRADE);
        VERSION_GRADE.put("二年级", SMALL_GRADE);
        VERSION_GRADE.put("三年级", SMALL_GRADE);
        VERSION_GRADE.put("四年级", SMALL_GRADE);
        VERSION_GRADE.put("五年级", SMALL_GRADE);
        VERSION_GRADE.put("六年级", SMALL_GRADE);

        VERSION_GRADE.put("七年级", MIDDLE_GRADE);
        VERSION_GRADE.put("八年级", MIDDLE_GRADE);
        VERSION_GRADE.put("九年级", MIDDLE_GRADE);

        VERSION_GRADE.put("高一", HIGH_GRADE);
        VERSION_GRADE.put("高二", HIGH_GRADE);
        VERSION_GRADE.put("高三", HIGH_GRADE);
    }

    private GradeUtil() {
    }
}
