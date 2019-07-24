package com.zhidejiaoyu.student.service;

import com.baomidou.mybatisplus.service.IService;
import com.zhidejiaoyu.common.pojo.ReadCourse;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpSession;

public interface ReadCourseService extends IService<ReadCourse> {

    ServerResponse<Object> getAllCourse(HttpSession session);

    ServerResponse<Object> updStudyPlan(HttpSession session, Long courseId);

    ServerResponse<Object> getStudyCourse(HttpSession session, Long courseId);
}
