package com.zhidejiaoyu.common.study.memorydifficulty;

import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 计算单词的记忆难度
 *
 * @author: wuchenxi
 * @date: 2020/1/3 11:31:31
 */
@Slf4j
@Component
public class WordMemoryDifficulty extends CheckMemoryDifficultyParam implements IMemoryDifficulty {

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Override
    public Integer getMemoryDifficulty(StudyCapacity studyCapacity) {

        if (!super.checkParam(studyCapacity)) {
            return 1;
        }

        try {
            Long studentId = studyCapacity.getStudentId();
            Long unitId = studyCapacity.getUnitId();
            Long id = studyCapacity.getWordId();
            int type = studyCapacity.getType();
            String studyModel = this.getStudyModel(type);
            if (studyModel == null) {
                return 0;
            }
            // 获取记忆强度
            Double memoryStrength = studyCapacity.getMemoryStrength();

            // 获取单词的错误次数
            Integer errCount = studyCapacity.getFaultTime();
            if (errCount == null) {
                return 0;
            }

            // 获取单词的学习次数
            Integer studyCount = learnExtendMapper.selectStudyCount(studyCapacity, studyModel);
            if (studyCount == null) {
                return 0;
            }

            // 保存记忆追踪时计算记忆难度：由于先保存的记忆追踪信息，其中的错误次数已经+1，而学习次数还是原来的，可能出现错误次数>学习次数的的情况，所以学习次数也要在原基础上+1
            if (errCount > studyCount) {
                studyCount++;
            }

            if (errCount > studyCount) {
                log.warn("学生 {} 在单元 {} 模块 {} 下的单词 {} 错误次数大于了学习次数！", studentId, unitId, studyModel, id);
                errCount = studyCount;
            }
            return getMemoryDifficulty(memoryStrength, errCount, studyCount);
        } catch (Exception e) {
            log.error("计算单词的记忆难度出错！", e);
        }
        return 0;
    }
}
