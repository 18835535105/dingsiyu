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

@Service(value = "breakThroughTheTextService")
public class BreakThroughTheTextServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Resource
    private SaveTeksData saveTeksData;
    private Integer type = 13;
    private Integer easyOrHard = 1;
    private String studyModel = "闯关测试";
    @Resource
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        currentDayOfStudyRedisOpt.saveStudyModel(student.getId(), studyModel, unitId);
        Long studentId = student.getId();
        return saveTeksData.getStudyModel(unitId, studentId, easyOrHard, type);
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue,
                            Integer plan, Integer total, Long courseId, Long flowId, Long[] errorId) {
        saveTeksData.saveStudy(session, unitId, flowId, studyModel, easyOrHard);
        return ServerResponse.createBySuccess();
    }
}
