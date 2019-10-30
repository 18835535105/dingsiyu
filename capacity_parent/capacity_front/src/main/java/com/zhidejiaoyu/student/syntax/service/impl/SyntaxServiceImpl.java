package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class SyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxService {

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;


    /**
     * 获取学生学习课程
     *
     * @param session
     * @return
     */
    @Override
    public Object getStudyCourse(HttpSession session) {
        Student student = getStudent(session);
        studentStudyPlanMapper.selectSyntaxByStudentAndType(student.getId(), 7);
        return null;
    }
}
