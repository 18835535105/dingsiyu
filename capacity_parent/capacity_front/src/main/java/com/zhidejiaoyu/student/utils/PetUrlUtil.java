package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.constant.PetImageConstant;
import org.springframework.util.Assert;

/**
 * 获取宠物图片地址
 *
 * @author wuchenxi
 * @date 2018/7/5
 */
public class PetUrlUtil {

    private static final int EIGHTY = 80;
    private static final int NINETY = 90;
    private static final int FULL = 100;
    /**
     * 获取测试结果页宠物图片
     *
     * @param student   学生信息
     * @param point 学生分数
     * @param testType 测试类型
     *                 <ul>
     *                 <li>摸底测试</li>
     *                 <li>单元闯关测试</li>
     *                 <li>阶段测试</li>
     *                 <li>智能复习测试 （测试复习）</li>
     *                 <li>已学测试</li>
     *                 <li>生词测试</li>
     *                 <li>熟词测试</li>
     *                 <li>五维测试</li>
     *                 </ul>
     * @return
     */
    public static String getTestPetUrl(Student student, Integer point, String testType) {
        Assert.notNull(student, "student can't be null!");
        Assert.notNull(point, "point can't be null!");
        String petName = student.getPetName();
        Assert.notNull(petName, "petName is null！");
        if (point < EIGHTY) {
            switch (petName) {
                case "李糖心":
                    return PetImageConstant.HONEY_NOT_PASS;
                case "威士顿":
                    return PetImageConstant.CAT_NOT_PASS;
                case "大明白":
                    return robotImg(point, testType);
                case "无名":
                    return PetImageConstant.HERO_NOT_PASS;
                default:
            }
        } else {
            switch (petName) {
                case "李糖心":
                    return PetImageConstant.HONEY_PASS;
                case "威士顿":
                    return PetImageConstant.CAT_PASS;
                case "大明白":
                    return robotImg(point, testType);
                case "无名":
                    return PetImageConstant.HERO_PASS;
                default:
            }
        }
        return null;
    }

    /**
     * 获取大明白图片
     *
     * @param point
     * @param testType
     * @return
     */
    private static String robotImg(Integer point, String testType) {
        switch (testType) {
            case "摸底测试":
                return robotLevelTestImgUrl(point);
            case "单元闯关测试":
                return robotUnitTestImgUrl(point);
            case "阶段测试":
                return robotStageTestImgUrl(point);
            case "智能复习测试":
                return robotReviewTestImgUrl(point);
            case "已学测试":
            case "生词测试":
            case "熟词测试":
            case "生句测试":
            case "熟句测试":
                return robotCenterTestImgUrl(point);
            case "五维测试":
                return robotFiveTestImgUrl(point);
            default:
        }
        return "";
    }

    /**
     * 五维测试
     *
     * @param point
     * @return
     */
    private static String robotFiveTestImgUrl(Integer point) {
        if (point < EIGHTY) {
            return PetImageConstant.INSIST;
        } else {
            return PetImageConstant.FOREVER;
        }
    }

    /**
     * 测试中心
     *
     * @param point
     * @return
     */
    private static String robotCenterTestImgUrl(Integer point) {
        if (point < EIGHTY) {
            return PetImageConstant.WALK;
        } else if (point < NINETY) {
            return PetImageConstant.DEFEAT;
        } else {
            return PetImageConstant.GOOD;
        }
    }

    /**
     * 智能复习测试
     *
     * @param point
     * @return
     */
    private static String robotReviewTestImgUrl(Integer point) {
        if (point < EIGHTY) {
            return PetImageConstant.HOWEVER;
        } else {
            return PetImageConstant.COOL;
        }
    }

    /**
     * 阶段测试
     *
     * @param point
     * @return
     */
    private static String robotStageTestImgUrl(Integer point) {
        if (point < EIGHTY) {
            return PetImageConstant.CONTINUE;
        } else {
            return PetImageConstant.SUCCEED;
        }
    }

    /**
     * 单元闯关测试
     *
     * @param point
     * @return
     */
    private static String robotUnitTestImgUrl(Integer point) {
        if (point < EIGHTY) {
            return PetImageConstant.LOSE;
        } else if (point < FULL) {
            return PetImageConstant.WIN;
        } else {
            return PetImageConstant.STAR;
        }
    }

    /**
     * 等级测试
     *
     * @param point
     * @return
     */
    private static String robotLevelTestImgUrl(Integer point) {
        if (point < EIGHTY) {
            return PetImageConstant.JIONG;
        } else if (point < NINETY) {
            return PetImageConstant.HAHA;
        } else {
            return PetImageConstant.BANG;
        }
    }
}
