package com.zhidejiaoyu.common.utils.goldUtil;

import com.zhidejiaoyu.common.mapper.StudentExpansionMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
import com.zhidejiaoyu.common.pojo.TestRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 判断当日闯关类金币是否可以继续增加工具类
 *
 * @author wuchenxi
 * @date 2019-03-27
 */
@Slf4j
@Component
public class TestGoldUtil {

    /**
     * 闯关类每日最多增加金币量
     */
    private static final int MAX_GOLD = 300;

    @Autowired
    private StudentExpansionMapper studentExpansionMapper;

    /**
     * 根据测试成绩计算奖励金币数
     *
     * @param isFirst         是否是第一次进行该模块下的单元闯关测试
     * @param wordUnitTestDTO
     * @param student
     * @param testRecord
     * @return 学生应奖励金币数
     */
    /*public int saveGold(boolean isFirst, WordUnitTestDTO wordUnitTestDTO, Student student, TestRecord testRecord) {
        int point = wordUnitTestDTO.getPoint();
        int goldCount = 0;
        if (isFirst) {
            goldCount = getGoldCount(wordUnitTestDTO, student, point);
        } else {
            // 查询当前单元测试历史最高分数
            int betterPoint = testRecordMapper.selectUnitTestMaxPointByStudyModel(student.getId(), wordUnitTestDTO.getUnitId()[0],
                    wordUnitTestDTO.getClassify());

            // 非首次测试成绩本次测试成绩大于历史最高分，超过历史最高分次数 +1并且金币奖励翻倍
            if (betterPoint < wordUnitTestDTO.getPoint()) {
                int betterCount = testRecord.getBetterCount() + 1;
                testRecord.setBetterCount(betterCount);
                goldCount = getGoldCount(wordUnitTestDTO, student, point);
            }
        }
        return testGoldUtil.addGold(student, goldCount);
    }

    private int getGoldCount(WordUnitTestDTO wordUnitTestDTO, Student student, int point) {
        int goldCount;
        if (point < SIX) {
            goldCount = 0;
        } else if (point < SEVENTY) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SIXTY_TO_SEVENTY;
        } else if (point < PASS) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_SEVENTY_TO_EIGHTY;
        } else if (point < NINETY_POINT) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_EIGHTY_TO_NINETY;
        } else if (point < FULL_MARK) {
            goldCount = TestAwardGoldConstant.UNIT_TEST_NINETY_TO_FULL;
        } else {
            goldCount = TestAwardGoldConstant.UNIT_TEST_FULL;
        }
        this.saveLog(student, goldCount, wordUnitTestDTO, null);
        return goldCount;
    }*/

    /**
     * 可奖励金币个数
     *
     * @param student
     * @param gold  当前测试获取金币数
     * @return
     */
    public int addGold(Student student, int gold) {
        if (gold == 0) {
            return 0;
        }
        Long studentId = student.getId();
        int addGold = 0;
        StudentExpansion studentExpansion = studentExpansionMapper.selectByStudentId(studentId);
        if (studentExpansion == null) {
            studentExpansion = new StudentExpansion();
            studentExpansion.setTestGoldAdd(gold);
            studentExpansion.setStudentId(studentId);
            addGold = gold;
            this.insertStudentExpansion(studentExpansion, student);
            return addGold;
        } else if (studentExpansion.getTestGoldAdd() == null) {
            studentExpansion.setTestGoldAdd(gold);
            this.updateStudentExpansion(studentExpansion, student);
            return gold;
        } else if (studentExpansion.getTestGoldAdd() < MAX_GOLD) {
            // 当日闯关获得金币加上当前测试获得金币大于最大值，取差值
            if (studentExpansion.getTestGoldAdd() + gold > MAX_GOLD) {
                addGold = MAX_GOLD - studentExpansion.getTestGoldAdd();
                studentExpansion.setTestGoldAdd(MAX_GOLD);
            } else {
                // 不足最大值直接加上当前测试获得金币数
                addGold = gold;
                studentExpansion.setTestGoldAdd(studentExpansion.getTestGoldAdd() + gold);
            }
            this.updateStudentExpansion(studentExpansion, student);
            return addGold;
        } else {
            log.error("学生[{}]->[{}] 今日闯关类测试获取金币数=300", student.getId(), student.getStudentName());
            return addGold;
        }
    }

    private void updateStudentExpansion(StudentExpansion studentExpansion, Student student) {
        try {
            studentExpansionMapper.updateById(studentExpansion);
        } catch (Exception e) {
            log.error("闯关类每日最多增加金币量更新学生扩展信息失败，[{}]->[{}]", student.getId(), student.getStudentName(), e);
        }
    }

    private void insertStudentExpansion(StudentExpansion studentExpansion, Student student) {
        try {
            studentExpansionMapper.insert(studentExpansion);
        } catch (Exception e) {
            log.error("闯关类每日最多增加金币量新增学生扩展信息失败，[{}]->[{}]", student.getId(), student.getStudentName(), e);
        }
    }
}
