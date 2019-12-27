package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudyCapacity;
import com.zhidejiaoyu.common.study.MemoryDifficultyUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.language.BaiduSpeak;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.RedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 *
 */
@Service(value = "memoryWordService")
@Slf4j
public class MemoryWordServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Resource
    private SaveData saveData;
    @Resource
    private LearnExtendMapper learnExtendMapper;
    @Resource
    private LearnNewMapper learnNewMapper;
    @Resource
    private UnitVocabularyNewMapper unitVocabularyNewMapper;
    @Resource
    private StudyCapacityMapper studyCapacityMapper;
    @Resource
    private RedisOpt redisOpt;
    private Integer type = 3;
    private Integer easyOrHard = 1;
    private String studyModel = "慧记忆";

    @Override
    public Object getStudy(HttpSession session, Long unitId,Integer difficulty) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        saveData.judgeIsFirstStudy(session, student);
        boolean firstStudy = redisOpt.getGuideModel(studentId, studyModel);
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
        return saveData.getNextMemoryWord(session, unitId, student, firstStudy, plan, wordCount, learnNews.getGroup(), type, studyModel);
    }


    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue,
                            Integer plan, Integer total, Long courseId, Long flowId) {
        Student student = getStudent(session);
        if (saveData.saveVocabularyModel(student, session, unitId, wordId, isTrue, plan, total,
                flowId, easyOrHard, type, studyModel)) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }


}
