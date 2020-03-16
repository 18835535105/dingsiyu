package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipTestService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class ShipTestServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ShipTestService {

    @Resource
    private StudentMapper studentMapper;
    @Override
    public Object getTest(HttpSession session, Long studentId) {
        Student student = getStudent(session);
        //查询一小时内的pk次数

        //查询pk信息
        //1.pk发起人的信息

        //2.被pk人的信息
        Student student1 = studentMapper.selectById(studentId);
        //3.查询题目

        //将查询到的题目返回


        return null;
    }
}
