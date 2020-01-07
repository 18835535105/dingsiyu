package com.zhidejiaoyu.common.study.memorydifficulty;

import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * 获取语法的记忆难度
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 14:01
 */
@Slf4j
@Component
public class SyntaxMemoryDifficulty extends CheckMemoryDifficultyParam implements IMemoryDifficulty {

    @Resource
    private LearnExtendMapper learnExtendMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Override
    public Integer getMemoryDifficulty(StudyCapacity studyCapacity) {
        if (!super.checkParam(studyCapacity)) {
            return 1;
        }

        int easyOrHard = 1;
        if (studyCapacity.getType().equals(22)) {
            easyOrHard = 2;
        }
        //获取当前learnId
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studyCapacity.getStudentId(), studyCapacity.getUnitId(), easyOrHard,4);
        // 获取当前学习次数
        Integer studyCount=learnExtendMapper.selectCountByLearnIdAndWordIdAndType(learnNew.getId(),studyCapacity.getWordId(),studyCapacity.getType());
        if (studyCapacity.getFaultTime() > studyCount) {
            studyCount = studyCapacity.getFaultTime() + 1;
        }
        return getMemoryDifficulty(studyCapacity.getMemoryStrength(), studyCapacity.getFaultTime(), studyCount);
    }
}
