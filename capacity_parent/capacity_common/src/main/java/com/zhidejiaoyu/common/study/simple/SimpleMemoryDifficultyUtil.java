package com.zhidejiaoyu.common.study.simple;

import com.zhidejiaoyu.common.mapper.simple.SimpleLearnMapper;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.LearnExample;
import com.zhidejiaoyu.common.pojo.SimpleCapacity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 计算指定单词/例句的记忆难度
 *
 * @author wuchenxi
 * @date 2018年5月11日
 */
@Component
@Slf4j
public class SimpleMemoryDifficultyUtil {


    @Autowired
    private SimpleLearnMapper learnMapper;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    /**
     * 计算当前单词/例句的记忆难度
     *
     * @param simpleCapacity 记忆追踪模块对象
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     * @return 当前单词的记忆难度 0:熟词；其余情况为生词
     */
    public int getMemoryDifficulty(SimpleCapacity simpleCapacity, Integer type) {
        if (simpleCapacity == null) {
            return 0;
        }
            // 获取记忆强度
        Double memoryStrength = simpleCapacity.getMemoryStrength();
            // 获取单词的错误次数
        Integer errCount = simpleCapacity.getFaultTime();
            // 获取单词的学习次数
        Integer studyCount = this.getStudyCount(simpleCapacity.getStudentId(), simpleCapacity.getUnitId(),
                simpleCapacity.getVocabularyId(), type);

        // 保存记忆追踪时计算记忆难度：由于先保存的记忆追踪信息，其中的错误次数已经+1，而学习次数还是原来的，可能出现错误次数>学习次数的的情况，所以学习次数也要在原基础上+1
        if (errCount > studyCount) {
            studyCount++;
        }

        if (errCount > studyCount) {
            log.error("学生 {} 在单元 {} 模块 {} 下的单词 {} 错误次数大于了学习次数！", simpleCapacity.getStudentId(),
                    simpleCapacity.getUnitId(), simpleCommonMethod.getTestType(type), simpleCapacity.getVocabularyId());
            errCount = studyCount;
        }

        // 错误率
        double errorRate = (errCount * 1.0) / (studyCount * 1.0);

        if (memoryStrength == 1 || errorRate == 0) {
            return 0;
        } else if (errorRate == 1) {
            return 5;
        } else if (errorRate < 1 && errorRate >= 0.8) {
            return 4;
        } else if (errorRate < 0.8 && errorRate >= 0.6) {
            return 3;
        } else if (errorRate < 0.6 && errorRate >= 0.4) {
            return 2;
        } else if (errorRate < 0.4 && errorRate > 0) {
            return 1;
        }
        return 0;
    }



    /**
     * 获取学生在当前单元下学习当前单词的次数
     *
     * @param studentId
     * @param unitId
     * @param id         当前单词/例句id
     * @param type
     * @return
     */
    private Integer getStudyCount(Long studentId, Long unitId, Long id, Integer type) {
        LearnExample learnExample = new LearnExample();
        learnExample.createCriteria().andStudentIdEqualTo(studentId).andUnitIdEqualTo(unitId)
                .andVocabularyIdEqualTo(id).andStudyModelEqualTo(simpleCommonMethod.getTestType(type));

        List<Learn> learns = learnMapper.selectByExample(learnExample);
        return learns.size() > 0 ? learns.get(0).getStudyCount() : 1;
    }
}
