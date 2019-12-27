package com.zhidejiaoyu.student.business.service.studyModelImpl;

import com.zhidejiaoyu.common.mapper.LearnNewMapper;
import com.zhidejiaoyu.common.pojo.LearnNew;
import com.zhidejiaoyu.student.business.service.IStudyService;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service(value = "sentencePatternTranslationService")
@Slf4j
public class SentencePatternTranslationServiceImpl extends BaseServiceImpl<LearnNewMapper, LearnNew> implements IStudyService {
    @Override
    public Object getStudy(HttpSession session, Long unitId) {
        return null;
    }

    @Override
    public Object saveStudy(HttpSession session, Long unitId, Long wordId, boolean isTrue, Integer plan, Integer total, Long courseId, Long flowId) {
        return null;
    }
}
