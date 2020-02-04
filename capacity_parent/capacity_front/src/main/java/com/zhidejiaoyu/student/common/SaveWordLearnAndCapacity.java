package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.StudentRestudyUtil;
import com.zhidejiaoyu.common.study.memorystrength.StudyMemoryStrength;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 学习单词模块，保存单词学习信息和慧追踪信息
 *
 * @author wuchenxi
 * @date 2018/7/5
 */
@Slf4j
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
    private CapacityPictureMapper capacityPictureMapper;

    @Resource
    private StudentRestudyUtil studentRestudyUtil;

    @Resource
    private StudyMemoryStrength studyMemoryStrength;

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

        if (studyModel != 1 && studyModel != 2 && studyModel != 3 && studyModel != 0) {
            throw new RuntimeException("studyModel=" + studyModel + " 非法！");
        }

        // 通过学生id，单元id和单词id获取当前单词的记忆追踪信息
        CapacityMemory capacity = getCapacityInfo(learn, student, studyModel, vocabulary);
        String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(learn.getUnitId(), vocabulary.getId());
        // 封装记忆追踪信息
        capacity = packageCapacityInfo(learn, student, isKnown, studyModel, vocabulary, capacity, wordChinese);
        return capacity;
    }

    private CapacityMemory packageCapacityInfo(Learn learn, Student student, boolean isKnown, Integer studyModel,
                                               Vocabulary vocabulary, CapacityMemory capacity, String wordChinese) {
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

                Object object = HttpUtil.getHttpSession().getAttribute(SessionConstant.FIRST_FALSE_ADD);
                capacity.setMemoryStrength((object == null || !(boolean) object) ? 0.12 : 0.62);
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
                } else {
                    // 单词图鉴
                    capacityPictureMapper.insert((CapacityPicture) capacity);
                }

            }

        } else {
            // 保存学生复习记录
            studentRestudyUtil.saveWordRestudy(learn, student, vocabulary.getWord(), 2);

            // 认识该单词
            if (isKnown) {
                // 重新计算黄金记忆点时间
                double memoryStrength = capacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacity.setPush(push);

                // 重新计算记忆强度
                capacity.setMemoryStrength(studyMemoryStrength.getMemoryStrength(memoryStrength, true));
            } else {
                // 错误次数在原基础上 +1
                int afterFaultTime = capacity.getFaultTime() + 1;
                if (learn.getStudyCount() != null && afterFaultTime > learn.getStudyCount()) {
                    afterFaultTime = learn.getStudyCount();
                }
                capacity.setFaultTime(afterFaultTime);
                // 重新计算黄金记忆点时间
                double memoryStrength = capacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                capacity.setPush(push);

                // 重新计算记忆强度
                capacity.setMemoryStrength(studyMemoryStrength.getMemoryStrength(memoryStrength, false));
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

    private CapacityMemory getCapacityInfo(Learn learn, Student student, Integer studyModel, Vocabulary vocabulary) {
        CapacityMemory capacity = null;
        if (studyModel == 1) {
            // 慧记忆
            List<CapacityMemory> capacityMemoryList = capacityMemoryMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
            if (capacityMemoryList.size() > 1) {
                capacityMemoryMapper.deleteById(capacityMemoryList.get(1).getId());
                capacity = capacityMemoryList.get(0);
            } else if (capacityMemoryList.size() > 0) {
                capacity = capacityMemoryList.get(0);
            }
        } else if (studyModel == 2) {
            // 慧听写
            List<CapacityListen> capacityListens = capacityListenMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
            if (capacityListens.size() > 1) {
                capacityListenMapper.deleteById(capacityListens.get(1).getId());
                capacity = capacityListens.get(0);
            } else if (capacityListens.size() > 0) {
                capacity = capacityListens.get(0);
            }
        } else if (studyModel == 3) {
            // 慧默写
            List<CapacityWrite> capacityWrites = capacityWriteMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
            if (capacityWrites.size() > 1) {
                capacityWriteMapper.deleteById(capacityWrites.get(1).getId());
                capacity = capacityWrites.get(0);
            } else if (capacityWrites.size() > 0) {
                capacity = capacityWrites.get(0);
            }
        } else {
            // 单词图鉴
            List<CapacityPicture> capacityPictures = capacityPictureMapper.selectByUnitIdAndId(student.getId(), learn.getUnitId(),
                    vocabulary.getId());
            if (capacityPictures.size() > 1) {
                capacityPictureMapper.deleteById(capacityPictures.get(1).getId());
                capacity = capacityPictures.get(0);
            } else if (capacityPictures.size() > 0) {
                capacity = capacityPictures.get(0);
            }
        }
        return capacity;
    }
}
