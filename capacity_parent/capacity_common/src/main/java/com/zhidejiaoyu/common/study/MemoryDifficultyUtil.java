package com.zhidejiaoyu.common.study;

import com.zhidejiaoyu.common.mapper.LearnMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.simple.SimpleLearnMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.simple.SimpleCommonMethod;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.List;

/**
 * 计算指定单词/例句的记忆难度
 *
 * @author wuchenxi
 * @date 2018年5月11日
 */
@Slf4j
@Component
public class MemoryDifficultyUtil {

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private SimpleLearnMapper simpleLearnMapper;

    @Autowired
    private SimpleCommonMethod simpleCommonMethod;

    @Autowired
    private StudentMapper studentMapper;

    /**
     * 计算同步版当前单词/例句的记忆难度
     *
     * @param object 记忆追踪模块对象
     * @param flag   1:计算单词的记忆难度；2：计算例句的记忆难度
     * @return 当前单词的记忆难度 0:熟词；其余情况为生词
     */
    public int getMemoryDifficulty(Object object, Integer flag) throws RuntimeException {
        if (object == null) {
            return 0;
        }
        try {
            Long studentId = (Long) this.getFieldValue(object, object.getClass().getField("studentId"));
            Long unitId = (Long) this.getFieldValue(object, object.getClass().getField("unitId"));
            Long id = (Long) this.getFieldValue(object, object.getClass().getField("vocabularyId"));
            String studyModel = "";
            if (object instanceof CapacityListen) {
                studyModel = "慧听写";
            } else if (object instanceof CapacityWrite) {
                studyModel = "慧默写";
            } else if (object instanceof CapacityPicture) {
                studyModel = "单词图鉴";
            } else if (object instanceof CapacityMemory) {
                studyModel = "慧记忆";
            } else if (object instanceof SentenceWrite) {
                studyModel = "例句默写";
            } else if (object instanceof SentenceTranslate) {
                studyModel = "例句翻译";
            } else if (object instanceof SentenceListen) {
                studyModel = "例句听力";
            }
            // 获取记忆强度
            Double memoryStrength = (Double) this.getFieldValue(object, object.getClass().getField("memoryStrength"));

            // 获取单词的错误次数
            Integer errCount = (Integer) this.getFieldValue(object, object.getClass().getField("faultTime"));

            // 获取单词的学习次数
            Integer studyCount = this.getStudyCount(studentId, unitId, id, flag, studyModel);

            // 保存记忆追踪时计算记忆难度：由于先保存的记忆追踪信息，其中的错误次数已经+1，而学习次数还是原来的，可能出现错误次数>学习次数的的情况，所以学习次数也要在原基础上+1
            if (errCount > studyCount) {
                studyCount ++;
            }

            if (errCount > studyCount) {
                log.warn("学生 {} 在单元 {} 模块 {} 下的单词 {} 错误次数大于了学习次数！", studentId, unitId, studyModel, id);
                errCount = studyCount;
            }
            return getMemoryDifficulty(memoryStrength, errCount, studyCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getMemoryDifficulty(Double memoryStrength, Integer errCount, Integer studyCount) {
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
     * 获取属性值
     *
     * @param object 需要操作的目标对象
     * @param field  属性对象
     */
    Object getFieldValue(Object object, Field field) {
        field.setAccessible(true);
        try {
            return field.get(object);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取学生在当前单元下学习当前单词的次数
     *
     * @param studentId
     * @param unitId
     * @param id         当前单词/例句id
     * @param flag
     * @param studyModel
     * @return
     */
    private Integer getStudyCount(Long studentId, Long unitId, Long id, Integer flag, String studyModel) {
        LearnExample learnExample = new LearnExample();
        if (flag == 1) {
            learnExample.createCriteria().andStudentIdEqualTo(studentId).andUnitIdEqualTo(unitId)
                    .andVocabularyIdEqualTo(id).andStudyModelEqualTo(studyModel);
        } else {
            learnExample.createCriteria().andStudentIdEqualTo(studentId).andUnitIdEqualTo(unitId)
                    .andExampleIdEqualTo(id).andStudyModelEqualTo(studyModel);
        }

        List<Learn> learns = learnMapper.selectByExample(learnExample);
        return learns.size() > 0 ? learns.get(0).getStudyCount() : 1;
    }

    /**
     * 计算清学版当前单词/例句的记忆难度
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
            Student student = studentMapper.selectById(simpleCapacity.getStudentId());
            if (student != null) {
                log.warn("学生 [{} - {} - {}] 在单元 {} 模块 {} 下的单词 {} 错误次数大于了学习次数！", student.getId(), student.getAccount(), student.getStudentName(),
                        simpleCapacity.getUnitId(), simpleCommonMethod.getTestType(type), simpleCapacity.getVocabularyId());
            } else {
                log.warn("学生 [{}] 在单元 {} 模块 {} 下的单词 {} 错误次数大于了学习次数！", simpleCapacity.getStudentId(),
                        simpleCapacity.getUnitId(), simpleCommonMethod.getTestType(type), simpleCapacity.getVocabularyId());
            }

            errCount = studyCount;
        }

        return getMemoryDifficulty(memoryStrength, errCount, studyCount);
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

        List<Learn> learns = simpleLearnMapper.selectByExample(learnExample);
        return learns.size() > 0 ? learns.get(0).getStudyCount() : 1;
    }
}
