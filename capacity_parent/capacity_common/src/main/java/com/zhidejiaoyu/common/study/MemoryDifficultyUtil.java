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
import java.util.Date;
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
     * @param studyCapacity 记忆追踪模块对象
     * @param flag   1:计算单词的记忆难度；2：计算例句的记忆难度
     * @return 当前单词的记忆难度 0:熟词；其余情况为生词
     */
    public int getMemoryDifficulty(StudyCapacity studyCapacity, Integer flag) throws RuntimeException {
        if (studyCapacity == null) {
            return 0;
        }
        try {
            Long studentId = studyCapacity.getStudentId();
            Long unitId = studyCapacity.getUnitId();
            Long id = studyCapacity.getWordId();
            int type=studyCapacity.getType();
            String studyModel = "";
            /**
             * 2，单词播放机  3，慧记忆 4，会听写
             *      *                 5，慧默写 6，单词游戏 7，句型翻译 8，句型听力 9，音译练习
             *      *                 10，句型默写 11，课文试听 12，课文训练 13，闯关测试 14，课文跟读
             *      *                 15，读语法 16，选语法 17，写语法 18，语法游戏
             */
            switch (type){
                case 1:
                    studyModel="单词播放机";
                    break;
                case 1:
                    studyModel="单词播放机";
                    break;
            }
            // 获取记忆强度
            Double memoryStrength = studyCapacity.getMemoryStrength();

            // 获取单词的错误次数
            Integer errCount = studyCapacity.getFaultTime();

            // 获取单词的学习次数
            Integer studyCount = this.getStudyCount(studentId, unitId, id, flag, studyModel);

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
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取阅读模块下生词手册中单词的记忆强度
     *
     * @param readWord
     * @return
     */
    public int getReadWordDifficulty(ReadWord readWord) {
        if (readWord == null) {
            log.error("获取学生阅读生词强化记忆难度数据时，readWord=null");
            return 0;
        }
        Integer type = readWord.getType();
        String studyModel = getReadWordStudyModel(type);

        if (studyModel == null) {
            log.warn("未获取到学生阅读生词手册类型！type=[{}]", type);
            return 0;
        }

        Learn learn = learnMapper.selectReadWord(readWord, studyModel);
        int studyCount;
        if (learn == null) {
            studyCount = 1;
            this.saveLearn(readWord);
        } else {
            studyCount = learn.getStudyCount();
        }
        Integer errorCount = readWord.getErrorCount();
        if (errorCount > studyCount) {
            log.warn("学生 {} 在单元 课程 {} 模块 {} 下的单词 {} 错误次数大于了学习次数！", readWord.getStudentId(), readWord.getCourseId(), studyModel, readWord.getWordId());
            errorCount = studyCount;
        }
        return getMemoryDifficulty(readWord.getMemoryStrength(), errorCount, studyCount);
    }

    private void saveLearn(ReadWord readWord) {
        Date date = new Date();
        Learn learn = new Learn();
        learn.setStudentId(readWord.getStudentId());
        learn.setStudyCount(1);
        learn.setStatus(0);
        learn.setCourseId(readWord.getCourseId());
        learn.setLearnCount(1);
        learn.setVocabularyId(readWord.getWordId());
        learn.setType(1);
        learn.setUpdateTime(date);
        learn.setUnitId(0L);
        learn.setStudyModel(this.getReadWordStudyModel(readWord.getType()));
        learn.setFirstIsKnown(0);
        learn.setFirstStudyTime(date);
        learn.setLearnTime(date);
        learnMapper.insert(learn);
    }

    /**
     * 获取生词手册类型
     *
     * @param type
     * @return
     */
    public String getReadWordStudyModel(Integer type) {
        switch (type) {
            case 1:
                return "阅读生词手册-慧记忆";
            case 2:
                return "阅读生词手册-单词图鉴";
            case 3:
                return "阅读生词手册-慧听写";
            case 4:
                return "阅读生词手册-慧默写";
            default:
                return null;
        }
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
     * @param type           1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
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
     * @param id        当前单词/例句id
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
