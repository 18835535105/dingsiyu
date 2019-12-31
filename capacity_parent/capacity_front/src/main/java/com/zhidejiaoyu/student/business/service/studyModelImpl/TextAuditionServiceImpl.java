package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveTeksData;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service(value = "textAuditionService")
public class TextAuditionServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Resource
    private SaveTeksData saveTeksData;
    private Integer type = 11;
    private Integer easyOrHard = 1;
    private String studyModel = "课文试听";

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        return saveTeksData.getSudyModel(session, unitId, student, studentId, studyModel, easyOrHard, type);
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId) {
        saveTeksData.saveStudy(session, unitId, flowId,studyModel);
        return ServerResponse.createBySuccess();
    }
}
