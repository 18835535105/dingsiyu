package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.mapper.UnitSentenceNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Sentence;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.utils.server.TestResponseCode;
import com.zhidejiaoyu.common.vo.student.SentenceTranslateVo;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveSentenceData;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Service(value = "sentencePatternTranslationService")
@Slf4j
public class SentencePatternTranslationServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {

    @Resource
    private RedisOpt redisOpt;
    @Resource
    private SaveData saveData;
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
    private Integer type = 7;
    private Integer easyOrHard = 1;
    private String studyModel = "例句翻译";


    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {

        Student student = getStudent(session);
        Long studentId = student.getId();
        boolean firstStudy = redisOpt.getGuideModel(studentId, studyModel);
        // 记录学生开始学习该例句的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());

        //获取当前单元下的learnId
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitId(studentId, unitId, easyOrHard);
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
        // 获取当前单元已学习的当前模块的例句id
        Sentence sentence = saveSentenceData.getSentence(unitId, student, learnNews.getGroup(), type, studyModel);
        SentenceTranslateVo sentenceTranslateVo = saveSentenceData.getSentenceTranslateVo(plan.longValue(), firstStudy,
                sentenceCount.longValue(), type, sentence);
        sentenceTranslateVo.setStudyNew(true);
        return ServerResponse.createBySuccess(sentenceTranslateVo);
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId) {
        Student student = getStudent(session);
        if (saveData.saveVocabularyModel(student, session, unitId, wordId, isTrue, plan, total,
                flowId, easyOrHard, type, studyModel)) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }
}
