package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface ShipAddEquipmentService extends BaseService<Student> {
    Object queryAddStudentEquipment(HttpSession session);

    Object strengthenStudentEquipment(HttpSession session, Long equipmentId);

    Object getEquipmentInterface(HttpSession session, Integer type);

    Object wearEquipment(HttpSession session, Long equipmentId);

    void updateLeaderBoards(Student student);
}
