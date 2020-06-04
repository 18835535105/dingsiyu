package com.zhidejiaoyu.student.business.learn.service.impl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.learn.common.SaveTeksData;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service(value = "textAuditionService")
public class TextAuditionServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Resource
    private SaveTeksData saveTeksData;
    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;
    private Integer type = 11;
    private Integer easyOrHard = 1;
    private String studyModel = "课文试听";

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        currentDayOfStudyRedisOpt.saveStudyModel(student.getId(), studyModel, unitId);
        return saveTeksData.getStudyModel(unitId, student.getId(), easyOrHard, type);
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId, Long[] errorIds) {
        saveTeksData.saveStudy(session, unitId, flowId, studyModel, easyOrHard);
        return ServerResponse.createBySuccess();
    }
}
