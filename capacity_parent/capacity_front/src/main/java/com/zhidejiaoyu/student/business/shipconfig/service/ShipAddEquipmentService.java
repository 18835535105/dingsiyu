package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Equipment;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

import javax.servlet.http.HttpSession;

public interface ShipAddEquipmentService extends BaseService<Student> {
    Object queryAddStudentEquipment(HttpSession session);

    Object strengthenStudentEquipment(HttpSession session, Long equipmentId);

    Object getEquipmentInterface(HttpSession session, Integer type);

    Object wearEquipment(HttpSession session, Long equipmentId, Integer type, String imgUrl);

    void updateLeaderBoards(Student student);

     void updateUseEqu(Student student, Equipment equipment);

    ServerResponse<Object> getEquipmentNexLevlInfromation(Long equipmentId, HttpSession session);

    /**
     * 分数足够添加的飞船物品
     * @param studentId
     * @return
     */
    Object getTestAddEqu(Long studentId);
}
