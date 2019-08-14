package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.common.constant.study.PointConstant;
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

    /**
     * 获取测试结果页宠物图片
     *
     * @param student  学生信息
     * @param point    学生分数
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
        switch (petName) {
            case "李糖心":
                return getHoneyImg(point);
            case "威士顿":
                return getCatImg(point);
            case "大明白":
                return robotImg(point, testType);
            case "无名":
                return getHeroImg(point);
            default:
        }
        return null;
    }

    /**
     * 获取无名测试结果页图片
     *
     * @param point
     * @return
     */
    private static String getHeroImg(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.HERO_NOT_PASS;
        }
        if (point < PointConstant.HUNDRED) {
            return PetImageConstant.HERO_PASS;
        }
        return PetImageConstant.HERO_ONE_HUNDRED;
    }

    /**
     * 获取威士顿测试结果页图片
     *
     * @param point
     * @return
     */
    private static String getCatImg(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.CAT_NOT_PASS;
        }
        if (point < PointConstant.HUNDRED) {
            return PetImageConstant.CAT_PASS;
        }
        return PetImageConstant.CAT_ONE_HUNDRED;
    }

    /**
     * 获取李糖心测试结果页图片
     *
     * @param point
     * @return
     */
    private static String getHoneyImg(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.HONEY_NOT_PASS;
        }
        if (point < PointConstant.HUNDRED) {
            return PetImageConstant.HONEY_PASS;
        }
        return PetImageConstant.HONEY_ONE_HUNDRED;
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
            case "学前测试":
            case "学后测试":
                return robotCourseTestImgUrl(point);
            case "单元前测":
                return robotBeforeUnitTestImgUrl(point);
            case "能力值测试":
                return robotWordTestIngUrl(point);
            default:
        }
        return "";
    }

    /**
     * 能力值测试
     *
     * @param point
     * @return
     */
    private static String robotWordTestIngUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.INSIST;
        }
        return PetImageConstant.FOREVER;
    }

    /**
     * 课程前测，后测
     *
     * @param point
     * @return
     */
    private static String robotCourseTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.HOWEVER;
        }
        return PetImageConstant.SUCCEED;
    }

    /**
     * 单元前测
     *
     * @param point
     * @return
     */
    private static String robotBeforeUnitTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.WALK;
        }
        if (point < PointConstant.NINETY) {
            return PetImageConstant.WIN;
        }
        return PetImageConstant.GOOD;
    }


    /**
     * 五维测试
     *
     * @param point
     * @return
     */
    private static String robotFiveTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.INSIST;
        }
        return PetImageConstant.FOREVER;
    }

    /**
     * 测试中心
     *
     * @param point
     * @return
     */
    private static String robotCenterTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.WALK;
        }
        if (point < PointConstant.NINETY) {
            return PetImageConstant.DEFEAT;
        }
        return PetImageConstant.GOOD;

    }

    /**
     * 智能复习测试
     *
     * @param point
     * @return
     */
    private static String robotReviewTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.HOWEVER;
        }
        return PetImageConstant.COOL;

    }

    /**
     * 阶段测试
     *
     * @param point
     * @return
     */
    private static String robotStageTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.CONTINUE;
        }
        return PetImageConstant.SUCCEED;

    }

    /**
     * 单元闯关测试
     *
     * @param point
     * @return
     */
    private static String robotUnitTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.LOSE;
        }
        if (point < PointConstant.HUNDRED) {
            return PetImageConstant.WIN;
        }
        return PetImageConstant.STAR;

    }

    /**
     * 等级测试
     *
     * @param point
     * @return
     */
    private static String robotLevelTestImgUrl(Integer point) {
        if (point < PointConstant.EIGHTY) {
            return PetImageConstant.JIONG;
        }
        if (point < PointConstant.NINETY) {
            return PetImageConstant.HAHA;
        }
        return PetImageConstant.BANG;

    }
}
