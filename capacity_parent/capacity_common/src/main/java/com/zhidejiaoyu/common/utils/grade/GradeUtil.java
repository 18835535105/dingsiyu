package com.zhidejiaoyu.common.utils.grade;

import com.zhidejiaoyu.common.constant.GradeNameConstant;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.StringUtil;
import lombok.NonNull;
import org.apache.commons.collections.CollectionUtils;

import java.util.*;

/**
 * 获取年级工具类
 *
 * @author: wuchenxi
 * @date: 2019/12/19 10:58:58
 */
public class GradeUtil {

    private static final String REN_JIAO_BAN_VERSION = "人教版";

    /**
     * 通用小学年级
     */
    private static final String[] SMALL_GRADE = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级"};

    /**
     * 通用初中年级
     */
    private static final String[] MIDDLE_GRADE = {"七年级", "八年级", "九年级"};

    /**
     * 通用高中年级
     */
    private static final String[] HIGH_GRADE = {"高一", "高二", "高三"};

    /**
     * 通用全部年级
     */
    private static final String[] NORMAL_GRADE = {"一年级", "二年级", "三年级", "四年级", "五年级", "六年级", "七年级", "八年级", "九年级", "高一", "高二", "高三"};

    /**
     * 人教版
     */
    private static final String[] REN_JIAO_BAN = {"七年级", "八年级", "九年级", "高中"};

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
     * 版本名与年级数据对应（不区分学段）
     */
    private static Map<String, String[]> ALL_PHASE_VERSION_GRADE;

    /**
     * 比当前年级高一个年级的年级名称，key:当前年级；value：下一年级
     */
    private static final Map<String, String> HIGHER_GRADE;

    /**
     * 将年级转换为对应的数字表示
     */
    private static final Map<String, Integer> GRADE_TO_NUM;

    static {
        initVersionGrade();

        initAllPhaseVersionGrade();

        HIGHER_GRADE = new HashMap<>(16);
        HIGHER_GRADE.put("一年级", "二年级");
        HIGHER_GRADE.put("二年级", "三年级");
        HIGHER_GRADE.put("三年级", "四年级");
        HIGHER_GRADE.put("四年级", "五年级");
        HIGHER_GRADE.put("五年级", "六年级");
        HIGHER_GRADE.put("六年级", "七年级");
        HIGHER_GRADE.put("初中", "初中");
        HIGHER_GRADE.put("七年级", "八年级");
        HIGHER_GRADE.put("八年级", "九年级");
        HIGHER_GRADE.put("九年级", "高一");
        HIGHER_GRADE.put("高一", "高二");
        HIGHER_GRADE.put("高二", "高三");
        HIGHER_GRADE.put("高三", "高三");
        HIGHER_GRADE.put("高中", "高中");

        GRADE_TO_NUM = new HashMap<>(16);
        GRADE_TO_NUM.put("一年级", 1);
        GRADE_TO_NUM.put("二年级", 2);
        GRADE_TO_NUM.put("三年级", 3);
        GRADE_TO_NUM.put("四年级", 4);
        GRADE_TO_NUM.put("五年级", 5);
        GRADE_TO_NUM.put("六年级", 6);
        GRADE_TO_NUM.put("初中", 9);
        GRADE_TO_NUM.put("七年级", 7);
        GRADE_TO_NUM.put("八年级", 8);
        GRADE_TO_NUM.put("九年级", 9);
        GRADE_TO_NUM.put("高一", 10);
        GRADE_TO_NUM.put("高二", 11);
        GRADE_TO_NUM.put("高三", 12);
        GRADE_TO_NUM.put("高中", 12);
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
     * 获取比当前年级高一个年级的年级集合（不区分学段），比如当前年级是8年级，获取1~9年级集合
     *
     * @param version
     * @param grade
     * @return
     */
    public static List<String> highThanCurrentAllPhase(String version, String grade) {
        if (HIGHER_GRADE.containsKey(grade)) {
            grade = HIGHER_GRADE.get(grade);
        }
        return smallThanCurrentAllPhase(version, grade);
    }

    /**
     * 获取小于或等于当前年级的年级集合（不区分学段），比如当前年级是8年级，获取1~8年级集合
     *
     * @param version
     * @param grade
     * @return
     */
    public static List<String> smallThanCurrentAllPhase(String version, String grade) {

        // 人教版特殊年级处理
        if (Objects.equals(REN_JIAO_BAN_VERSION, version)) {
            if (Objects.equals(grade, GradeNameConstant.SENIOR_ONE) || Objects.equals(grade, GradeNameConstant.SENIOR_TWO) || Objects.equals(grade, GradeNameConstant.SENIOR_THREE)) {
                grade = GradeNameConstant.HIGH;
            }
        }

        String[] gradeArr = ALL_PHASE_VERSION_GRADE.get(version);
        if (gradeArr != null) {
            return getGradeList(grade, gradeArr);
        }

        return getGradeList(grade, ALL_PHASE_VERSION_GRADE.get(grade));
    }

    /**
     * 小于或者等于当前年级的所有年级
     *
     * @param grade 年级
     * @return 如果没有查询到小于指定年级的年级数据，返回当前年级
     */
    public static List<String> smallThanCurrentGrade(String grade) {
        List<String> gradeList = new ArrayList<>();
        for (String s : NORMAL_GRADE) {
            if (Objects.equals(s, grade)) {
                return gradeList;
            }
            gradeList.add(s);
        }
        if (CollectionUtils.isEmpty(gradeList)) {
            gradeList.add(grade);
        }
        return gradeList;
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

    /**
     * 年级比较
     *
     * @param grade1 年级1
     * @param grade2 年级2
     * @return <ul>
     * <li>>0：grade1>grade2</li>
     * <li><=0：grade1<=grade2</li>
     * </ul>
     */
    public static int compareGrade(@NonNull String grade1, @NonNull String grade2) {
        Integer num1 = GRADE_TO_NUM.get(grade1);
        Integer num2 = GRADE_TO_NUM.get(grade2);

        if (num1 == null) {
            throw new ServiceException(grade1 + " 年级参数非法！");
        }

        if (num2 == null) {
            throw new ServiceException(grade2 + " 年级参数非法！");
        }

        return num1 - num2;
    }

    private static void initAllPhaseVersionGrade() {
        ALL_PHASE_VERSION_GRADE = new HashMap<>(16);
        ALL_PHASE_VERSION_GRADE.put("朗文国际", LANGWEN);
        ALL_PHASE_VERSION_GRADE.put("剑桥少儿英语（西安交大版）", JIANQIAO);
        ALL_PHASE_VERSION_GRADE.put("新概念经典版（中学）", MIDDLE_SCHOOL);
        ALL_PHASE_VERSION_GRADE.put("剑桥英语青少版", IN_THE_WHOLE_PERIOD);
        ALL_PHASE_VERSION_GRADE.put(REN_JIAO_BAN_VERSION, REN_JIAO_BAN);

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

    /**
     * 获取下一个年级
     *
     * @param grade
     * @return
     */
    public static String getNextGrade(String grade) {
        if (StringUtil.isEmpty(grade)) {
            return grade;
        }

        if (Objects.equals(grade, GradeNameConstant.SENIOR_THREE)) {
            return grade;
        }
        int length = NORMAL_GRADE.length;
        for (int i = 0; i < length; i++) {
            if (Objects.equals(grade, NORMAL_GRADE[i]) && i < length - 1) {
                return NORMAL_GRADE[i + 1];
            }
        }
        return grade;
    }

    private GradeUtil() {
    }

    public static void main(String[] args) {
//        System.out.println(GradeUtil.smallThanCurrentAllPhase("人教版", "高三"));
//        System.out.println(GradeUtil.highThanCurrentAllPhase("人教版(PEP)", "七年级"));
//        System.out.println(GradeUtil.smallThanCurrentGrade("高三").toString());
//        System.out.println(GradeUtil.smallThanCurrentGrade("九年级").toString());
//        System.out.println(GradeUtil.smallThanCurrentGrade("六年级").toString());
        System.out.println(GradeUtil.getNextGrade("一年级"));
        System.out.println(GradeUtil.getNextGrade("六年级"));
        System.out.println(GradeUtil.getNextGrade("九年级"));
        System.out.println(GradeUtil.getNextGrade("高三"));
    }
}
