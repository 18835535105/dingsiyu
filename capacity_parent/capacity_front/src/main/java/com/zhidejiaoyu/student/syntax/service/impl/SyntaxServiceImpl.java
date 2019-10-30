package com.zhidejiaoyu.student.syntax.service.impl;

import com.zhidejiaoyu.common.mapper.StudentStudyPlanMapper;
import com.zhidejiaoyu.common.mapper.StudentStudySyntaxMapper;
import com.zhidejiaoyu.common.mapper.SyntaxTopicMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentStudySyntax;
import com.zhidejiaoyu.common.pojo.SyntaxTopic;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.syntax.constant.SyntaxModelNameConstant;
import com.zhidejiaoyu.student.syntax.service.SyntaxService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.Objects;

@Service
public class SyntaxServiceImpl extends BaseServiceImpl<SyntaxTopicMapper, SyntaxTopic> implements SyntaxService {

    @Resource
    private StudentStudyPlanMapper studentStudyPlanMapper;

    @Resource
    private StudentStudySyntaxMapper studentStudySyntaxMapper;


    /**
     * 获取学生学习课程
     *
     * @param session
     * @return
     */
    @Override
    public Object getStudyCourse(HttpSession session) {
        Student student = getStudent(session);
        studentStudyPlanMapper.selectSyntaxByStudentAndType(student.getId());
        return null;
    }

    @Override
    public ServerResponse getSyntaxNode(Long unitId) {

        Student student = super.getStudent(HttpUtil.getHttpSession());

        StudentStudySyntax studentStudySyntax = studentStudySyntaxMapper.selectByStudentIdAndUnitId(student.getId(), unitId);
        if (Objects.isNull(studentStudySyntaxMapper)) {
            return ServerResponse.createBySuccessMessage(SyntaxModelNameConstant.GAME);
        }

        return ServerResponse.createBySuccessMessage(studentStudySyntax.getModel());
    }
}
