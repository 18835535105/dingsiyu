package com.zhidejiaoyu.student.common;

import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.simple.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.simple.CommonMethod;
import com.zhidejiaoyu.common.study.simple.GoldMemoryTime;
import com.zhidejiaoyu.common.study.simple.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.study.simple.MemoryStrengthUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 测试结束保存学习记忆追踪和测试记忆追踪
 *
 * @author wuchenxi
 * @date 2018/6/29 14:25
 */
@Component
@Slf4j
public class SaveLearnAndCapacity {

    @Autowired
    private SimpleCapacityMapper simpleCapacityMapper;

    @Autowired
    private MemoryDifficultyUtil memoryDifficultyUtil;

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private MemoryStrengthUtil memoryStrengthUtil;

    @Autowired
    private VocabularyMapper vocabularyMapper;

    @Autowired
    private CommonMethod commonMethod;

    @Autowired
    private UnitVocabularyMapper unitVocabularyMapper;

    @Autowired
    private StudentRestudyMapper studentRestudyMapper;

    @Autowired
    private DailyAwardAsync awardAsync;

    @Autowired
    private ExecutorService executorService;


    /**
     * 学习模块保存指定模块的学习记录和慧追踪信息
     *
     * @param learn
     * @param isTrue
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     * @return
     */
    public void saveLearnCapacity(HttpSession session, Learn learn, boolean isTrue, Integer type) {

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        // 获取学习记录
        Learn learnRecord = learnMapper.selectLearnRecord(student, learn.getUnitId(), learn.getVocabularyId(), commonMethod.getTestType(type));
        // 无该学习记录
        if (learnRecord == null) {
            learn.setStudyModel(commonMethod.getTestType(type));
            learn.setStudentId(student.getId());
            learn.setStudyCount(1);
            learn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
            learn.setUpdateTime(new Date());
            if (isTrue) {
                learn.setStatus(1);
            } else {
                learn.setStatus(0);
            }
            learn.setType(1);
            learnMapper.insert(learn);

            saveCapacityMemory(learn, student, isTrue, type);
        } else {
            saveLearnAndCapacity(session, student, learn.getUnitId(), learn.getVocabularyId(), type, isTrue);
        }

        // 判断学生今日复习30个生词且记忆强度达到50%
        awardAsync.todayReview(student);
    }

    /**
     * 测试模块 保存学习记录和记忆追踪信息
     *
     * @param correctWord  答对的单词/例句
     * @param errorWord     答错的单词/例句
     * @param correctWordId 答对的单词/例句 id
     * @param errorWordId   答错的单词/例句 id
     * @param session       session信息
     * @param unitId        如果单元id长度为1，说明当前测试是以单元为单位的测试；如果长度大于1，说明当前测试是以课程为单位的测试
     * @param type   1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     * @return 响应信息
     */
    public void saveTestAndCapacity(String[] correctWord, String[] errorWord, Long[] correctWordId,
                                                       Long[] errorWordId, HttpSession session,
                                                       Long[] unitId, Integer type) {

        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);

        log.info("======== 测试模块 保存学习记录和记忆追踪信息 ========");
        log.info("correctWordId:{}; errorWordId:{}; studentId:{}; unitId:{}; type:{};", correctWordId, errorWordId, student.getId(), unitId, type);

        // 保存正确单词/例句的学习记录和记忆追踪信息
        if (correctWord != null && correctWordId != null && correctWord.length > 0
                && correctWord.length == correctWordId.length) {
            int correctWordLength = correctWord.length;
            for (int i = 0; i < correctWordLength; i++) {
                if (unitId.length == 1) {
                    this.saveLearnAndCapacity(session, student, unitId[0], correctWordId[i], type, true);
                } else {
                    this.saveLearnAndCapacity(session, student, unitId[i], correctWordId[i], type, true);
                }
            }
        }

        // 保存错误单词/例句的学习记录和记忆追踪信息
        if (errorWord != null && errorWordId != null && errorWord.length > 0
                && errorWord.length == errorWordId.length) {
            int errorWordLength = errorWord.length;
            for (int i = 0; i < errorWordLength; i++) {
                if (unitId.length == 1) {
                    this.saveLearnAndCapacity(session, student, unitId[0], errorWordId[i], type, false);
                } else {
                    this.saveLearnAndCapacity(session, student, unitId[i], errorWordId[i], type, false);
                }
            }
        }

        // 判断学生今日复习30个生词且记忆强度达到50%
        awardAsync.todayReview(student);
    }

    /**
     * 保存学习记录和记忆追踪记录
     *
     * @param session session 信息
     * @param student 学生信息
     * @param unitId  单词/例句所属单元id
     * @param wordId      单词或句子的id
     * @param isTrue  题目是否答对
     */
    private int saveLearnAndCapacity(HttpSession session, Student student, Long unitId, Long wordId, Integer type,
                                     boolean isTrue) {
        // 查询学习记录
        Learn learn = learnMapper.selectLearnRecord(student, unitId, wordId, commonMethod.getTestType(type));
        if(learn == null){
            log.error("学生[{}]-[{}]没有当前模块学习记录：单元id[{}],单词id[{}],模块type[{}]", student.getId(), student.getStudentName(), unitId, wordId, type);
            return 0;
        }

        // 保存记忆追踪信息
        SimpleCapacity capacity = saveCapacityMemory(learn, student, isTrue, type);

        // 计算记忆难度
        Integer memoryDifficult = 0;
        if (capacity != null) {
            memoryDifficult = memoryDifficultyUtil.getMemoryDifficulty(capacity, type);
        }
        // 更新学习记录
        learn.setLearnTime((Date) session.getAttribute(TimeConstant.BEGIN_START_TIME));
        learn.setStudyCount(learn.getStudyCount() + 1);
        if (memoryDifficult == 0) {
            // 熟词
            learn.setStatus(1);
        } else {
            learn.setStatus(0);
        }
        learn.setUpdateTime(new Date());
        return learnMapper.updateByPrimaryKeySelective(learn);
    }

    /**
     * 保存记忆追踪数据
     *
     * @param learn    学习信息
     * @param student  学生信息
     * @param isKnown  是否答对该单词/例句 true：答对；false：答错
     * @param type 1:单词辨音; 2:词组辨音; 3:快速单词; 4:快速词组; 5:词汇考点; 6:快速句型; 7:语法辨析; 8单词默写; 9:词组默写;
     */
    private SimpleCapacity saveCapacityMemory(Learn learn, Student student, boolean isKnown, Integer type) {

        Vocabulary vocabulary = vocabularyMapper.selectByWordInfo(learn.getVocabularyId());

        // 获取当前内容的记忆追踪信息
        SimpleCapacity simpleCapacity = null;
        List<SimpleCapacity> simpleCapacities = simpleCapacityMapper.selectSimpleCapacityRecord(student.getId(),
                learn.getVocabularyId(), learn.getUnitId(), type);
        if (simpleCapacities.size() > 1) {
            simpleCapacityMapper.deleteById(simpleCapacities.get(1).getId());
            simpleCapacity = simpleCapacities.get(0);
        } else if (simpleCapacities.size() == 1) {
            simpleCapacity = simpleCapacities.get(0);
        }

        if (simpleCapacity == null) {
            if (!isKnown) {
                // 记忆追踪中没有当前单词信息
                simpleCapacity = new SimpleCapacity();
                simpleCapacity.setCourseId(learn.getCourseId());
                simpleCapacity.setFaultTime(1);
                simpleCapacity.setMemoryStrength(0.12);
                simpleCapacity.setPush(GoldMemoryTime.getGoldMemoryTime(0.12, new Date()));
                simpleCapacity.setStudentId(student.getId());
                simpleCapacity.setType(type);
                simpleCapacity.setUnitId(learn.getUnitId());
                simpleCapacity.setVocabularyId(learn.getVocabularyId());
                simpleCapacity.setWord(vocabulary.getWord());

                String wordChinese = unitVocabularyMapper.selectWordChineseByUnitIdAndWordId(learn.getUnitId(), learn.getVocabularyId());
                if (StringUtils.isNotEmpty(wordChinese)) {
                    simpleCapacity.setWordChinese(wordChinese);
                } else {
                    simpleCapacity.setWordChinese(vocabulary.getWordChinese());
                }

                simpleCapacityMapper.insert(simpleCapacity);
                return simpleCapacity;
            }
        } else {
            // 记忆追踪汇总已有当前单词信息
            if (!isKnown) {
                simpleCapacity.setFaultTime(simpleCapacity.getFaultTime() + 1);
            }
            // 重新计算黄金记忆点时间
            double memoryStrength = simpleCapacity.getMemoryStrength();
            Date push = GoldMemoryTime.getGoldMemoryTime(memoryStrength, new Date());
            simpleCapacity.setPush(push);

            // 重新计算记忆强度
            simpleCapacity.setMemoryStrength(memoryStrengthUtil.getTestMemoryStrength(memoryStrength, isKnown));
            simpleCapacityMapper.updateById(simpleCapacity);

            // 保存学生复习记录
            saveStudentRestudy(learn, student, vocabulary, type);

            return simpleCapacity;
        }
        return null;
    }

    private void saveStudentRestudy(Learn learn, Student student, Vocabulary vocabulary, Integer type) {
        StudentRestudy studentRestudy = new StudentRestudy();
        studentRestudy.setCourseId(learn.getCourseId());
        studentRestudy.setStudentId(student.getId());
        if (type == 6 || type == 7) {
            studentRestudy.setType(2);
        } else {
            studentRestudy.setType(1);
        }
        studentRestudy.setUnitId(learn.getUnitId());
        studentRestudy.setUpdateTime(new Date());
        studentRestudy.setVersion(1);
        studentRestudy.setVocabularyId(learn.getVocabularyId());
        studentRestudy.setWord(vocabulary.getWord());
        try {
            studentRestudyMapper.insert(studentRestudy);
        } catch (Exception e) {
            log.error("保存学生复习记录失败，学生信息：[{}]-[{}]=[{}], learn=[{}], vocabulary=[{}]",
                    student.getAccount(), student.getId(), student.getStudentName(), learn.toString(), vocabulary.toString());
        }
    }
}
