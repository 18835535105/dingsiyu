package com.zhidejiaoyu.student.business.learn.service.impl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.learn.common.SaveData;
import com.zhidejiaoyu.student.business.learn.common.SaveSentenceData;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.learn.vo.GetVo;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service(value = "sentencePatternListeningService")
@Slf4j
public class SentencePatternListeningServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {

    @Resource
    private SaveData saveData;
    @Resource
    private SaveSentenceData saveSentenceData;
    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;
    private final Integer type = 8;
    private final Integer easyOrHard = 1;
    private final String studyModel = "例句听力";
    private final Integer modelType = 2;

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        currentDayOfStudyRedisOpt.saveStudyModel(student.getId(), studyModel, unitId);
        return saveSentenceData.getStudyModel(session, unitId, difficulty, student, student.getId(), studyModel, easyOrHard, type);

    }

    @Override
    public Object saveStudy(HttpSession session, GetVo getVo) {
        Student student = getStudent(session);
        getVo.setEasyOrHard(easyOrHard);
        getVo.setType(type);
        getVo.setStudyModel(studyModel);
        getVo.setModel(modelType);
        if (saveData.saveVocabularyModel(student, session, getVo)) {
            return ServerResponse.createBySuccess();
        }
        return ServerResponse.createByErrorMessage("学习记录保存失败");
    }
}
