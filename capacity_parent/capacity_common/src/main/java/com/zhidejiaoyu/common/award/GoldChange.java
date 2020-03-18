package com.zhidejiaoyu.common.award;

import com.zhidejiaoyu.common.constant.TestAwardGoldConstant;
import com.zhidejiaoyu.common.constant.study.PointConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.goldUtil.StudentGoldAdditionUtil;

/**
 * 获取应该奖励的金币数
 *
 * @author: wuchenxi
 * @date: 2019/12/19 15:27:27
 */
public class GoldChange {


    /**
     * 单词单元闯关根据分数获取应奖励金币数
     *
     * @param point
     * @return
     */
    public static int getWordUnitTestGold(Student student, int point) {
        int goldCount;
        if (point < PointConstant.SIXTY) {
            goldCount = 0;
        } else if (point < PointConstant.SEVENTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SIXTY_TO_SEVENTY;
        } else if (point < PointConstant.EIGHTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SEVENTY_TO_EIGHTY;
        } else if (point < PointConstant.NINETY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_NINETY;
        } else if (point < PointConstant.HUNDRED) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_NINETY_TO_FULL;
        } else {
            goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
        }
        if (student.getBonusExpires() != null) {
            goldCount=StudentGoldAdditionUtil.getGoldAddition(student, goldCount + 0.0).intValue();
        }
        return goldCount;
    }
}
