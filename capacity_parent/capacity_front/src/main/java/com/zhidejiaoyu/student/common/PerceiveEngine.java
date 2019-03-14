package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.study.CommonMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 认知引擎计算公共类
 *
 * @author wuchenxi
 * @date 2018/10/27
 */
@Component
public class PerceiveEngine {

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private DurationMapper durationMapper;

    @Autowired
    private CommonMethod commonMethod;

    /**
     * 获取当前学习模块的认知引擎级别
     *
     * 学习速度 v ：当前模块今日学习单词数/当前模块今日学习的有效时长   (个/小时)
     * 记忆引擎： v <= 100  1
     * v <= 300 2
     * v <= 500 3
     * v <= 700 4
     * v > 700 5
     *
     * @param student
     * @param model   学习模块    1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写
     * @param unitId
     * @return
     */
    public int getPerceiveEngine(Student student, Integer model, Long unitId) {
        // 当前模块今日学习的单词数
        int learnCount = learnMapper.countTodayLearnedByStudyModel(student.getId(), unitId, commonMethod.getTestType(model));

        // 当前模块学习的今日有效时长
        Long sumValidTime = durationMapper.sumTodayModelValidTime(student.getId(), model + 13, unitId);

        if (learnCount == 0 || sumValidTime == null) {
            return 3;
        }

        int speed = (int) Math.round(learnCount / (sumValidTime / 3600D));
        if (speed <= 100) {
            return 1;
        } else if (speed <= 300) {
            return 2;
        } else if (speed <= 500) {
            return 3;
        } else if (speed <= 700) {
            return 4;
        } else {
            return 5;
        }
    }
}
