package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.BaseUtil.SaveModel.SaveData;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
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
    private Integer model = 1;
    private Integer type = 3;
    private Integer easyOrHard = 1;
    private String studyModel = "慧记忆";

    @Override
    public Object getStudy(HttpSession session, Long unitId) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        saveData.judgeIsFirstStudy(session, student);
        return null;
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
