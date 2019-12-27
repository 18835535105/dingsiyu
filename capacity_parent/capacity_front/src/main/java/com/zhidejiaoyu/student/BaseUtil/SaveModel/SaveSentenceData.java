package com.zhidejiaoyu.student.BaseUtil.SaveModel;

import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.SentenceMapper;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.study.CommonMethod;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.testUtil.TestResultUtil;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@Component
public class SaveSentenceData {

    private final String STUDYMODEL1 = "例句翻译";
    private final String STUDYMODEL2 = "例句听力";
    private final String STUDYMODEL3 = "例句默写";

    @Resource
    private SentenceMapper sentenceMapper;
    @Resource
    private BaiduSpeak baiduSpeak;
    @Resource
    private TestResultUtil testResultUtil;
    @Resource
    private CommonMethod commonMethod;
    @Resource
    private LearnExtendMapper learnExtendMapper;

    /**
     * 返回记忆达到黄金记忆点的例句信息
     *
     * @param studyCapacity 记忆追踪
     * @param plan          进度
     * @param firstStudy    是否是第一次学习
     * @param sentenceCount 当前单元例句总数
     * @param type
     * @return 例句翻译学习页面展示信息
     */

    public ServerResponse<SentenceTranslateVo> returnGoldWord(StudyCapacity studyCapacity, Long plan, boolean firstStudy,
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
        return ServerResponse.createBySuccess(null);
    }


    public SentenceTranslateVo getSentenceTranslateVo(Long plan, boolean firstStudy, Long sentenceCount, Integer type, Sentence sentence) {
        SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVos(plan, firstStudy, sentenceCount, sentence, 0.0);
        int nextInt = new Random().nextInt(10);
        if (nextInt > 2) {
            testResultUtil.getOrderEnglishList(sentenceTranslateVo, sentence.getCentreExample(), sentence.getExampleDisturb(), type);
        } else {
            testResultUtil.getOrderChineseList(sentenceTranslateVo, sentence.getCentreTranslate(), sentence.getTranslateDisturb(), type);
        }
        return sentenceTranslateVo;
    }

    public SentenceTranslateVo getSentenceTranslateVos(Long plan, boolean firstStudy, Long sentenceCount, Sentence sentence, double memoryStrength) {
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
        sentenceTranslateVo.setStudyNew(false);
        testResultUtil.getOrderEnglishList(sentenceTranslateVo, sentence.getCentreExample(), sentence.getTranslateDisturb(), type);
        return sentenceTranslateVo;
    }

    private SentenceTranslateVo getSentenceVo(Sentence sentence, boolean firstStudy, Long plan, double memoryStrength, Long sentenceCount, Integer type) {
        SentenceTranslateVo sentenceTranslateVo = getSentenceTranslateVos(plan, firstStudy, sentenceCount, sentence, memoryStrength);
        sentenceTranslateVo.setStudyNew(false);
        sentenceTranslateVo.setEnglishList(commonMethod.getEnglishList(sentence.getCentreExample()));
        if (type == 2) {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), sentence.getExampleDisturb()));
        } else {
            sentenceTranslateVo.setOrderEnglish(commonMethod.getOrderEnglishList(sentence.getCentreExample(), null));
        }
        return sentenceTranslateVo;
    }

    public Sentence getSentence(Long unitId, Student student, Integer group, Integer type, String studyModel) {
        // 查询学习记录本模块学习过的所有单词id
        List<Long> wordIds = learnExtendMapper.selectByUnitIdAndStudentIdAndType(unitId, student.getId(), studyModel);
        return sentenceMapper.selectOneWordNotInIdsNew(wordIds, unitId, group);
    }
}
