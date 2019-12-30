package com.zhidejiaoyu.common.study.memorydifficulty;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.common.constant.studycapacity.StudyCapacityTypeConstant;
import com.zhidejiaoyu.common.constant.syntax.SyntaxModelNameConstant;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.LearnExtend;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
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
    private LearnMapper learnMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private LearnNewMapper learnNewMapper;

    @Override
    public Integer getMemoryDifficulty(StudyCapacity studyCapacity) {
        if (!super.checkParam(studyCapacity)) {
            return 1;
        }

        Integer easyOrHard = 1;
        if (studyCapacity.getType().equals(22)) {
            easyOrHard = 2;
        }
        //获取当前learnId
        LearnNew learnNew = learnNewMapper.selectByStudentIdAndUnitId(studyCapacity.getStudentId(), studyCapacity.getUnitId(), easyOrHard);
        // 获取当前学习次数
        Integer studyCount=learnExtendMapper.selectCountByLearnIdAndWordIdAndType(learnNew.getId(),studyCapacity.getWordId(),studyCapacity.getType());
        if (studyCapacity.getFaultTime() > studyCount) {
            studyCount = studyCapacity.getFaultTime() + 1;
        }
        return getMemoryDifficulty(studyCapacity.getMemoryStrength(), studyCapacity.getFaultTime(), studyCount);
    }

    /**
     * 匹配学习模块
     *
     * @param type
     * @return
     */
    private String getStudyModel(Integer type) {
        if (Objects.equals(type, StudyCapacityTypeConstant.LEARN_SYNTAX)) {
            return SyntaxModelNameConstant.LEARN_SYNTAX;
        }
        if (Objects.equals(type, StudyCapacityTypeConstant.SELECT_SYNTAX)) {
            return SyntaxModelNameConstant.SELECT_SYNTAX;
        }
        if (Objects.equals(type, StudyCapacityTypeConstant.WRITE_SYNTAX)) {
            return SyntaxModelNameConstant.WRITE_SYNTAX;
        }
        log.error("语法学习模块type值出错，type={}", type);
        return SyntaxModelNameConstant.LEARN_SYNTAX;
    }
}
