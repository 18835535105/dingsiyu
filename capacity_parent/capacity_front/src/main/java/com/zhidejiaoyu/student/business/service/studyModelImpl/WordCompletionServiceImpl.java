package com.zhidejiaoyu.student.business.service.studyModelImpl;


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

@Service("wordCompletionService")
@Slf4j
public class WordCompletionServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {

    @Resource
    private SaveData saveData;
    private Integer type = 6;
    private Integer easyOrHard = 1;
    private String studyModel = "单词填字";

    @Override
    public Object getStudy(HttpSession session, Long unitId, Integer difficulty) {
        Student student = getStudent(session);
        Long studentId = student.getId();
        return saveData.getStudyWord(session, unitId, student, studentId,easyOrHard,studyModel,type);

    }



    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId) {
        return null;
    }
}
