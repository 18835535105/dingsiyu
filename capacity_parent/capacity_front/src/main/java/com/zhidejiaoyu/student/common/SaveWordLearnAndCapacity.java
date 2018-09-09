package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.MemoryStrengthUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 学习单词模块，保存单词学习信息和慧追踪信息
 *
 * @author wuchenxi
 * @date 2018/7/5
 */
@Component
public class SaveWordLearnAndCapacity {

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CapacityMemoryMapper capacityMemoryMapper;

    @Autowired
    private CapacityWriteMapper capacityWriteMapper;

    @Autowired
    private CapacityListenMapper capacityListenMapper;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private MemoryStrengthUtil memoryStrengthUtil;

    @Autowired
    private CapacityPictureMapper capacityPictureMapper;

    /**
     * 保存指定模块的单词学习记录和慧追踪信息
     *
     * @param learn
     * @param student
     * @param isKnown
     * @param studyModel 0：单词图鉴；1：慧记忆；2：慧听写；3：慧默写；
     * @return
     */
    public CapacityMemory saveCapacityMemory(Learn learn, Student student, boolean isKnown, Integer studyModel) {
        Vocabulary vocabulary = vocabularyMapper.selectByPrimaryKey(learn.getVocabularyId());
        // 通过学生id，单元id和单词id获取当前单词的记忆追踪信息
        CapacityMemory capacity;
        if (studyModel != 1 && studyModel != 2 && studyModel != 3 && studyModel != 0) {

            throw new RuntimeException("studyModel=" + studyModel + " 非法！");
        }

        if (studyModel == 1) {
            // 慧记忆
            capacity = capacityMemoryMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
        } else if (studyModel == 2) {
            // 慧听写
            capacity = capacityListenMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
        } else if (studyModel == 3) {
            // 慧默写
            capacity = capacityWriteMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
        } else {
            // 单词图鉴
            capacity = capacityPictureMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
        }
        String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(learn.getUnitId(), vocabulary.getId());
        if (capacity == null) {
            if (!isKnown) {
                if (studyModel == 1) {
                    // 慧记忆
                    capacity = new CapacityMemory();
                } else if (studyModel == 2) {
                    // 慧听写
                    capacity = new CapacityListen();
                } else if (studyModel == 3) {
                    // 慧默写
                    capacity = new CapacityWrite();
                } else {
                    // 单词图鉴
                    capacity = new CapacityPicture();
                }
                capacity.setCourseId(learn.getCourseId());
                capacity.setFaultTime(1);
                capacity.setMemoryStrength(0.12);
                capacity.setStudentId(student.getId());
                capacity.setUnitId(learn.getUnitId());
                capacity.setVocabularyId(learn.getVocabularyId());
                capacity.setWord(vocabulary.getWord());
                capacity.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
                capacity.setWordChinese(wordChinese);

                Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());
                capacity.setPush(push);

                if (studyModel == 1) {
                    // 慧记忆
                    capacityMemoryMapper.insert(capacity);
                } else if (studyModel == 2) {
                    // 慧听写
                    capacityListenMapper.insert((CapacityListen) capacity);
                } else if (studyModel == 3) {
                    // 慧默写
                    capacityWriteMapper.insert((CapacityWrite) capacity);
                }else {
                    // 单词图鉴
                    capacityPictureMapper.insert((CapacityPicture) capacity);
                }

            }

        } else {
            // 认识该单词
            if (isKnown) {
                // 重新计算黄金记忆点时间
                double memoryStrength = capacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacity.setPush(push);

                // 重新计算记忆强度
                capacity.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, true));
            } else {
                // 错误次数在原基础上 +1
                capacity.setFaultTime(capacity.getFaultTime() + 1);
                // 重新计算黄金记忆点时间
                double memoryStrength = capacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacity.setPush(push);

                // 重新计算记忆强度
                capacity.setMemoryStrength(memoryStrengthUtil.getStudyMemoryStrength(memoryStrength, false));
            }
            if (studyModel == 1) {
                // 慧记忆
                capacityMemoryMapper.updateByPrimaryKeySelective(capacity);
            } else if (studyModel == 2) {
                // 慧听写
                capacityListenMapper.updateByPrimaryKeySelective((CapacityListen) capacity);
            } else if (studyModel == 3) {
                // 慧默写
                capacityWriteMapper.updateByPrimaryKeySelective((CapacityWrite) capacity);
            } else {
                // 单词图鉴
                capacityPictureMapper.updateByPrimaryKeySelective((CapacityPicture) capacity);
            }
        }
        return capacity;
    }
}
