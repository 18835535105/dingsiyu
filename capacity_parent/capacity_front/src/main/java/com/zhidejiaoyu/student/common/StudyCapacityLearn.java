package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.constant.session.SessionConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.GoldMemoryTime;
import com.zhidejiaoyu.common.study.StudentRestudyUtil;
import com.zhidejiaoyu.common.study.memorystrength.StudyMemoryStrength;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class StudyCapacityLearn {


    @Autowired
    private VocabularyMapper vocabularyMapper;


    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Resource
    private StudyCapacityMapper studyCapacityMapper;

    @Resource
    private StudentRestudyUtil studentRestudyUtil;

    @Resource
    private StudyMemoryStrength studyMemoryStrength;
    @Resource
    private SentenceMapper sentenceMapper;
    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;

    /**
     * 保存指定模块的单词学习记录和慧追踪信息
     *
     * @param learn
     * @param student
     * @param isKnown
     * @param studyModel 0：单词图鉴；1：慧记忆；2：慧听写；3：慧默写；
     * @return
     */
    public StudyCapacity saveCapacityMemory(LearnNew learn, LearnExtend learnExtend, Student student, boolean isKnown, Integer studyModel) {
        if (studyModel < 7) {
            Vocabulary vocabulary = vocabularyMapper.selectByPrimaryKey(learnExtend.getWordId());
//        if (studyModel != 1 && studyModel != 2 && studyModel != 3 && studyModel != 4 && studyModel != 5) {
//            throw new RuntimeException("studyModel=" + studyModel + " 非法！");
//        }
            // 通过学生id，单元id和单词id获取当前单词的记忆追踪信息
            StudyCapacity capacity = getCapacityInfo(learn, student, studyModel, vocabulary);
            String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(learn.getUnitId(), vocabulary.getId());
            // 封装记忆追踪信息
            if (wordChinese == null) {
                wordChinese = vocabulary.getWordChinese();
            }
            capacity = packageCapacityInfo(learn, learnExtend, student, isKnown, studyModel, vocabulary, capacity, wordChinese);
            return capacity;
        } else if (studyModel < 11) {
            Sentence sentence = sentenceMapper.selectById(learnExtend.getWordId());
            // 通过学生id，单元id和单词id获取当前单词的记忆追踪信息
            StudyCapacity capacity = getCapacityInfo(learn, student, studyModel, sentence);
            String chinese = unitSentenceNewMapper.selectSentenceChineseByUnitIdAndSentenceId(learn.getUnitId(), sentence.getId());
            capacity = packageCapacityInfo(learn, learnExtend, student, isKnown, studyModel, sentence, capacity, chinese);
            return capacity;
        }

        return null;
    }

    private StudyCapacity getCapacityInfo(LearnNew learn, Student student, Integer studyModel, Sentence sentence) {
        StudyCapacity capacity = null;
        List<StudyCapacity> studyCapacities = studyCapacityMapper.selectByStudentIdAndUnitIdAndWordIdAndType(student.getId(), learn.getUnitId(),
                sentence.getId(), studyModel);
        if (studyCapacities.size() > 1) {
            studyCapacityMapper.deleteById(studyCapacities.get(1).getId());
            capacity = studyCapacities.get(0);
        } else if (studyCapacities.size() > 0) {
            capacity = studyCapacities.get(0);
        }
        return capacity;
    }

    private StudyCapacity packageCapacityInfo(LearnNew learn, LearnExtend learnExtend, Student student, boolean isKnown, Integer studyModel,
                                              Vocabulary vocabulary, StudyCapacity studyCapacity, String wordChinese) {
        if (studyCapacity == null) {
            if (!isKnown) {
                studyCapacity = new StudyCapacity();

                if (studyModel < 7) {
                    // 单词首次答错判断记忆强度是否需要在原基础上 +50%
                    Object object = HttpUtil.getHttpSession().getAttribute(SessionConstant.FIRST_FALSE_ADD);
                    studyCapacity.setMemoryStrength((object == null || !(boolean) object) ? 0.12 : 0.62);
                } else {
                    studyCapacity.setMemoryStrength(0.12);
                }

                studyCapacity.setCourseId(learn.getCourseId());
                studyCapacity.setFaultTime(1);
                studyCapacity.setType(studyModel);
                studyCapacity.setStudentId(student.getId());
                studyCapacity.setUnitId(learn.getUnitId());
                studyCapacity.setWordId(learnExtend.getWordId());
                studyCapacity.setWordChinese(wordChinese);
                Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());
                studyCapacity.setPush(push);
                studyCapacity.setWord(vocabulary.getWord());
                studyCapacity.setSyllable(StringUtils.isEmpty(vocabulary.getSyllable()) ? vocabulary.getWord() : vocabulary.getSyllable());
                studyCapacity.setUpdateTime(new Date());
                studyCapacity.setGroup(learn.getGroup());
                studyCapacityMapper.insert(studyCapacity);
            }
        } else {
            // 保存学生复习记录
            studentRestudyUtil.saveWordRestudy(learn, learnExtend, student, vocabulary.getWord(), 2);
            // 认识该单词
            if (isKnown) {
                // 重新计算黄金记忆点时间
                double memoryStrength = studyCapacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                studyCapacity.setPush(push);

                // 重新计算记忆强度
                studyCapacity.setMemoryStrength(studyMemoryStrength.getMemoryStrength(memoryStrength, true));
            } else {
                // 错误次数在原基础上 +1
                int afterFaultTime = studyCapacity.getFaultTime() + 1;
                if (learnExtend.getStudyCount() != null && afterFaultTime > learnExtend.getStudyCount()) {
                    afterFaultTime = learnExtend.getStudyCount();
                }
                studyCapacity.setFaultTime(afterFaultTime);
                // 重新计算黄金记忆点时间
                double memoryStrength = studyCapacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                studyCapacity.setPush(push);

                // 重新计算记忆强度
                studyCapacity.setMemoryStrength(studyMemoryStrength.getMemoryStrength(memoryStrength, false));
            }

            studyCapacityMapper.updateById(studyCapacity);
        }

        return studyCapacity;
    }

    private StudyCapacity packageCapacityInfo(LearnNew learn, LearnExtend learnExtend, Student student, boolean isKnown, Integer studyModel,
                                              Sentence sentence, StudyCapacity studyCapacity, String wordChinese) {
        if (studyCapacity == null) {
            if (!isKnown) {
                studyCapacity = new StudyCapacity();
                studyCapacity.setCourseId(learn.getCourseId());
                studyCapacity.setFaultTime(1);
                studyCapacity.setType(studyModel);
                studyCapacity.setMemoryStrength(0.12);
                studyCapacity.setStudentId(student.getId());
                studyCapacity.setUnitId(learn.getUnitId());
                studyCapacity.setWordId(learnExtend.getWordId());
                studyCapacity.setWordChinese(wordChinese);
                Date push = GoldMemoryTime.getGoldMemoryTime(0.12, new Date());
                studyCapacity.setPush(push);
                studyCapacity.setWord(sentence.getCentreTranslate());
                studyCapacityMapper.insert(studyCapacity);
            }
        } else {
            // 保存学生复习记录
            studentRestudyUtil.saveSentenceRestudy(learn, learnExtend, student, sentence.getCentreTranslate(), 2);
            // 认识该单词
            if (isKnown) {
                // 重新计算黄金记忆点时间
                double memoryStrength = studyCapacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                studyCapacity.setPush(push);

                // 重新计算记忆强度
                studyCapacity.setMemoryStrength(studyMemoryStrength.getMemoryStrength(memoryStrength, true));
            } else {
                // 错误次数在原基础上 +1
                int afterFaultTime = studyCapacity.getFaultTime() + 1;
                if (learnExtend.getStudyCount() != null && afterFaultTime > learnExtend.getStudyCount()) {
                    afterFaultTime = learnExtend.getStudyCount();
                }
                studyCapacity.setFaultTime(afterFaultTime);
                // 重新计算黄金记忆点时间
                double memoryStrength = studyCapacity.getMemoryStrength();
                Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
                studyCapacity.setPush(push);

                // 重新计算记忆强度
                studyCapacity.setMemoryStrength(studyMemoryStrength.getMemoryStrength(memoryStrength, false));
            }

            studyCapacityMapper.updateById(studyCapacity);
        }

        return studyCapacity;
    }

    private StudyCapacity getCapacityInfo(LearnNew learn, Student student, Integer studyModel, Vocabulary vocabulary) {
        StudyCapacity capacity = null;
        List<StudyCapacity> studyCapacities = studyCapacityMapper.selectByStudentIdAndUnitIdAndWordIdAndType(student.getId(), learn.getUnitId(),
                vocabulary.getId(), studyModel);
        if (studyCapacities.size() > 1) {
            studyCapacityMapper.deleteById(studyCapacities.get(1).getId());
            capacity = studyCapacities.get(0);
        } else if (studyCapacities.size() > 0) {
            capacity = studyCapacities.get(0);
        }
        return capacity;
    }
}