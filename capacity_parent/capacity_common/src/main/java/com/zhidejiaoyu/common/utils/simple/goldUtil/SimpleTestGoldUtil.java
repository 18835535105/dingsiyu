package com.zhidejiaoyu.common.utils.simple.goldUtil;

import com.zhidejiaoyu.common.mapper.simple.SimpleStudentExpansionMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentExpansion;
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
public class SimpleTestGoldUtil {

    /**
     * 闯关类每日最多增加金币量
     */
    private static final int MAX_GOLD = 300;

    @Autowired
    private SimpleStudentExpansionMapper simpleStudentExpansionMapper;

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
        StudentExpansion studentExpansion = simpleStudentExpansionMapper.selectByStudentId(studentId);
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
            simpleStudentExpansionMapper.updateById(studentExpansion);
        } catch (Exception e) {
            log.error("闯关类每日最多增加金币量更新学生扩展信息失败，[{}]->[{}]", student.getId(), student.getStudentName(), e);
        }
    }

    private void insertStudentExpansion(StudentExpansion studentExpansion, Student student) {
        try {
            simpleStudentExpansionMapper.insert(studentExpansion);
        } catch (Exception e) {
            log.error("闯关类每日最多增加金币量新增学生扩展信息失败，[{}]->[{}]", student.getId(), student.getStudentName(), e);
        }
    }
}
