package com.zhidejiaoyu.student.business.shipconfig.service;

import com.zhidejiaoyu.common.pojo.Equipment;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.WeekHistoryPlan;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;
import com.zhidejiaoyu.student.business.shipconfig.vo.EquipmentExperienceVo;

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
     * 计算指定日期数据数量
     * @param studentId
     * @param type
     * @param date
     * @return
     */
    long getEmpValue(Long studentId, Integer type, String date);

    Integer getWordAnPoint(int maxPonit, Long empValue, Integer point);

    Integer getTime(long studentId, String date, WeekHistoryPlan weekHistoryPlan, int validTimeMax, int type);

}
