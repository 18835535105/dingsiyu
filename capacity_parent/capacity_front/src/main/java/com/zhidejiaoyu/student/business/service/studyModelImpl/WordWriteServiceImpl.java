package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.mapper.LearnExtendMapper;
import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.mapper.StudyCapacityMapper;
import com.zhidejiaoyu.common.mapper.UnitVocabularyNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Date;

@Service(value = "wordWriteService")
@Slf4j
public class WordWriteServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Resource
    private SaveData saveData;
    @Resource
    private RedisOpt redisOpt;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    private Integer type = 5;
    private Integer easyOrHard = 2;
    private String studyModel = "慧默写";

    @Override
    public Object getStudy(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        boolean firstStudy = redisOpt.getGuideModel(studentId, studyModel);
        saveData.judgeIsFirstStudy(session, student);
        // 记录学生开始学习该单词的时间
        session.setAttribute(TimeConstant.BEGIN_START_TIME, new Date());
        //获取当前单元下的learnId
        LearnNew learnNews = learnNewMapper.selectByStudentIdAndUnitId(studentId, unitId, easyOrHard);
        // 查询学生当前单元下已学习单词的个数，即学习进度
        Integer plan = learnExtendMapper.countLearnWord(learnNews.getId(), unitId, learnNews.getGroup(), studyModel);
        // 获取当前单元下的所有单词的总个数
        Integer wordCount = unitVocabularyNewMapper.countByUnitId(unitId, learnNews.getGroup());
        if (wordCount == 0) {
            log.error("单元 {} 下没有单词信息！", unitId);
            return ServerResponse.createByErrorMessage("当前单元下没有单词！");
        }
        if (wordCount.equals(plan)) {
            return super.toUnitTest();
        }

        //获取单词id
        StudyCapacity studyCapacity = studyCapacityMapper.selectLearnHistory(unitId, studentId, DateUtil.DateTime(), type, easyOrHard, learnNews.getGroup());
        // 有到达黄金记忆点的单词优先复习
        if (studyCapacity != null) {
            // 返回达到黄金记忆点的单词信息
            return saveData.returnGoldWord(studyCapacity, plan.longValue(), firstStudy, wordCount.longValue(), type);
        }
        // 如果没有到达黄金记忆点的单词，获取当前学习进度的下一个单词
        return saveData.getNextMemoryWord(session, unitId, student, firstStudy, plan, wordCount, learnNews.getGroup(), type);
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId) {
        return null;
    }
}
