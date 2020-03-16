package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface ShipTestService extends BaseService<Student> {
    Object getTest(HttpSession session, Long studentId);
}
