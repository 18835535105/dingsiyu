package com.zhidejiaoyu.student.business.learn.service.impl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.learn.common.SaveData;
import com.zhidejiaoyu.student.business.learn.service.IStudyService;
import com.zhidejiaoyu.student.business.learn.vo.GetVo;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.CurrentDayOfStudyRedisOpt;
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
    private CurrentDayOfStudyRedisOpt currentDayOfStudyRedisOpt;
    private Integer type = 3;
    private Integer easyOrHard = 1;
    private String studyModel = "慧记忆";
    private Integer modelType = 1;

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        currentDayOfStudyRedisOpt.saveStudyModel(student.getId(), studyModel, unitId);
        // 判断学生是否在本系统首次学习，如果是记录首次学习时间
        return saveData.getStudyWord(session, unitId, student, student.getId(), easyOrHard, studyModel, type);
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
