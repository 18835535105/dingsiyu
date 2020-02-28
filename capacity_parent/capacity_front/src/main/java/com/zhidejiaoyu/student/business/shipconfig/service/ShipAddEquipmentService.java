package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface ShipAddEquipmentService extends BaseService<Student> {
    Object addStudentEquipment(HttpSession session, Long equipmentId);
}
