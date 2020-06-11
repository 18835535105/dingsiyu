package com.zhidejiaoyu.student.business.learn.common;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.common.utils.testUtil.TestResultUtil;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.student.common.CurrentDayOfStudyUtil;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class SaveSentenceData {

    private static final String STUDYMODEL1 = "例句翻译";
    private static final String STUDYMODEL2 = "例句听力";
    private static final String STUDYMODEL3 = "例句默写";

    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private UnitSentenceNewMapper unitSentenceNewMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private SaveSentenceData saveSentenceData;
    @Resource
    private SentenceMapper sentenceMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private TestResultUtil testResultUtil;
    @Resource
    private CommonMethod commonMethod;

    private static final String SENTENCE = "SENTENCE";
    @Resource
    private RedisOpt redisOpt;
    private final Integer modelType = 2;

    /**
     * @param session
     * @param unitId
     * @param difficulty 1:普通模式；2：暴走模式
     * @param student
     * @param studentId
     * @param studyModel
     * @param easyOrHard
     * @param type
     * @return
     */
    public ServerResponse<Object> getStudyModel(HttpSession session, Long unitId, Integer difficulty, Student student,
                                                Long studentId, String studyModel, Integer easyOrHard, Integer type) {
        boolean firstStudy = redisOpt.getGuideModel(studentId, studyModel);
        // 记录学生开始学习该例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        //获取当前单元下的learnId
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitIdAndEasyOrHardAndModelType(studentId, unitId, easyOrHard, modelType);
        // 查询学生当前单元下已学习单词的个数，即学习进度
        Integer plan = learnExtendMapper.countLearnWord(learnNews.getId(), unitId, learnNews.getGroup(), studyModel);
        // 获取当前单元下的所有单词的总个数
        Integer sentenceCount = unitSentenceNewMapper.countByUnitIdAndGroup(unitId, learnNews.getGroup());
        if (sentenceCount == 0) {
            log.error("单元 {} 下没有例句信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有例句！");
        }
        if (sentenceCount <= plan) {
            return ServerResponse.createBySuccess(TestResponseCode.TO_UNIT_TEST.getCode(), TestResponseCode.TO_UNIT_TEST.getMsg());
        }
        // 查看当前单元下记忆追踪中有无达到黄金记忆点的例句
        //获取单词id
        StudyCapacity studyCapacity = studyCapacityMapper.selectLearnHistory(unitId, studentId, DateUtil.DateTime(), type, easyOrHard, learnNews.getGroup());
        // 有到达黄金记忆点的例句优先复习
        if (studyCapacity != null) {
            // 返回达到黄金记忆点的例句信息
            //SentenceTranslate sentenceTranslate = sentenceTranslates.get(0);
            return saveSentenceData.returnGoldWord(studyCapacity, plan.longValue(), firstStudy, sentenceCount.longValue(), difficulty, studyModel);
        }
        // 获取当前学习进度的下一个例句
        Sentence sentence = saveSentenceData.getSentence(unitId, student, learnNews.getGroup(), studyModel);
        if (sentence == null) {
            return ServerResponse.createBySuccess(TestResponseCode.TO_UNIT_TEST.getCode(), TestResponseCode.TO_UNIT_TEST.getMsg());
        }
        if (type == 7) {
            SentenceTranslateVo sentenceTranslateVo = saveSentenceData.getSentenceTranslateVo(plan.longValue(), firstStudy,
                    sentenceCount.longValue(), difficulty, sentence);
            sentenceTranslateVo.setStudyNew(true);
            return ServerResponse.createBySuccess(sentenceTranslateVo);
        }
        if (type == 8) {

            return getSentenceTranslateVoServerResponse(firstStudy, plan.longValue(),
                    sentenceCount.longValue(), sentence, type);
        }
        if (type == 10) {
            return getSentenceTranslateVoServerResponse(firstStudy, plan.longValue(),
                    sentenceCount.longValue(), sentence, type);
        }
        return ServerResponse.createBySuccess();
    }

    /**
     * 返回记忆达到黄金记忆点的例句信息
     *
     * @param studyCapacity 记忆追踪
     * @param plan          进度
     * @param firstStudy    是否是第一次学习
     * @param sentenceCount 当前单元例句总数
     * @param type          1：普通模式；2：暴走模式
     * @return 例句翻译学习页面展示信息
     */

    private ServerResponse<Object> returnGoldWord(StudyCapacity studyCapacity, Long plan, boolean firstStudy,
                                                  Long sentenceCount, Integer type, String studyModel) {
        SentenceTranslateVo sentenceTranslateVo;
        // 例句翻译
        if (STUDYMODEL1.equals(studyModel)) {
            Sentence sentence = sentenceMapper.selectByPrimaryKey(studyCapacity.getWordId());
            // 计算当前例句的记忆强度
            double memoryStrength = studyCapacity.getMemoryStrength();
            sentenceTranslateVo = getSentenceTranslateVo(plan, firstStudy, sentenceCount, type, sentence);
            sentenceTranslateVo.setStudyNew(false);
            sentenceTranslateVo.setMemoryStrength(memoryStrength);
            sentenceTranslateVo.setCourseId(studyCapacity.getCourseId().intValue());
            sentenceTranslateVo.setUnitId(studyCapacity.getUnitId().intValue());
            return ServerResponse.createBySuccess(sentenceTranslateVo);
        } else if (STUDYMODEL2.equals(studyModel)) {
            // 例句听力
            // 计算当前例句的记忆强度
            Sentence sentence = sentenceMapper.selectByPrimaryKey(studyCapacity.getWordId());
            double memoryStrength = studyCapacity.getMemoryStrength();
            sentenceTranslateVo = this.getListenSentenceVo(sentence, firstStudy, plan, memoryStrength, sentenceCount, type);
            sentenceTranslateVo.setCourseId(studyCapacity.getCourseId().intValue());
            sentenceTranslateVo.setUnitId(studyCapacity.getUnitId().intValue());
            return ServerResponse.createBySuccess(sentenceTranslateVo);
        } else if (STUDYMODEL3.equals(studyModel)) {
            // 例句默写
            // 计算当前例句的记忆强度
            Sentence sentence = sentenceMapper.selectByPrimaryKey(studyCapacity.getWordId());
            double memoryStrength = studyCapacity.getMemoryStrength();
            sentenceTranslateVo = this.getSentenceVo(sentence, firstStudy, plan, memoryStrength, sentenceCount, type);
            return ServerResponse.createBySuccess(sentenceTranslateVo);
        }
        return ServerResponse.createBySuccess();
    }


    private SentenceTranslateVo getSentenceTranslateVo(Long plan, boolean firstStudy, Long sentenceCount, Integer type, Sentence sentence) {
        SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVos(plan, firstStudy, sentenceCount, sentence, 0.0);
        CurrentDayOfStudyUtil.saveSessionCurrent(SENTENCE, sentenceTranslateVo.getId());
        int nextInt = new Random().nextInt(10);
        if (nextInt > 2) {
            testResultUtil.getOrderEnglishList(sentenceTranslateVo, sentence.getCentreExample(), sentence.getExampleDisturb(), type);
        } else {
            testResultUtil.getOrderChineseList(sentenceTranslateVo, sentence.getCentreTranslate(), sentence.getTranslateDisturb(), type);
        }
        return sentenceTranslateVo;
    }

    private ServerResponse<Object> getSentenceTranslateVoServerResponse(boolean firstStudy, Long plan, Long sentenceCount, Sentence sentence, Integer type) {
        SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVos(plan, firstStudy, sentenceCount, sentence, 0.0);
        CurrentDayOfStudyUtil.saveSessionCurrent(SENTENCE, sentenceTranslateVo.getId());
        testResultUtil.getOrderEnglishList(sentenceTranslateVo, sentence.getCentreExample(), sentence.getExampleDisturb(), type);
        sentenceTranslateVo.setStudyNew(true);
        return ServerResponse.createBySuccess(sentenceTranslateVo);
    }

    private SentenceTranslateVo getSentenceTranslateVos(Long plan, boolean firstStudy, Long sentenceCount, Sentence sentence, double memoryStrength) {
        SentenceTranslateVo sentenceTranslateVo = new SentenceTranslateVo();
        sentenceTranslateVo.setChinese(sentence.getCentreTranslate().replace("*", ""));
        sentenceTranslateVo.setEnglish(sentence.getCentreExample().replace("#", " ").replace("*", " ").replace("$", ""));
        sentenceTranslateVo.setFirstStudy(firstStudy);
        sentenceTranslateVo.setSentence(sentence.getCentreExample().replace("#", " ").replace("*", " ").replace("$", ""));
        sentenceTranslateVo.setId(sentence.getId());
        sentenceTranslateVo.setPlan(plan);
        sentenceTranslateVo.setReadUrl(baiduSpeak.getSentencePath(sentence.getCentreExample()));
        sentenceTranslateVo.setSentenceCount(sentenceCount);
        sentenceTranslateVo.setMemoryStrength(memoryStrength);
        return sentenceTranslateVo;
    }

    private SentenceTranslateVo getListenSentenceVo(Sentence sentence, boolean firstStudy, Long plan, double memoryStrength, Long sentenceCount, Integer type) {
        SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVos(plan, firstStudy, sentenceCount, sentence, memoryStrength);
        CurrentDayOfStudyUtil.saveSessionCurrent(SENTENCE, sentenceTranslateVo.getId());
        sentenceTranslateVo.setStudyNew(false);
        testResultUtil.getOrderEnglishList(sentenceTranslateVo, sentence.getCentreExample(), sentence.getTranslateDisturb(), type);
        return sentenceTranslateVo;
    }

    private SentenceTranslateVo getSentenceVo(Sentence sentence, boolean firstStudy, Long plan, double memoryStrength, Long sentenceCount, Integer type) {
        SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVos(plan, firstStudy, sentenceCount, sentence, memoryStrength);
        CurrentDayOfStudyUtil.saveSessionCurrent(SENTENCE, sentenceTranslateVo.getId());
        sentenceTranslateVo.setStudyNew(false);
        sentenceTranslateVo.setEnglishList(commonMethod.getEnglishList(sentence.getCentreExample()));
        if (type == 2) {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
        } else {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
        }
        return sentenceTranslateVo;
    }

    private Sentence getSentence(Long unitId, Student student, Integer group, String studyModel) {
        // 查询学习记录本模块学习过的所有单词id
        List<Long> wordIds = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, student.getId(), studyModel, modelType);
        return sentenceMapper.selectOneWordNotInIdsNew(wordIds, unitId, group);
    }
}
