package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.common.mapper.StudentEquipmentMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.StudentEquipment;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@Service
public class ShipAddEquipmentServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements ShipAddEquipmentService {

    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;

    @Override
    public Object addStudentEquipment(HttpSession session, Long equipmentId) {

        Student student = getStudent(session);
        //获取学生有没有添加过该物品
        StudentEquipment studentEquipment = studentEquipmentMapper.selectByStudentIdAndEquipmentId(student.getId(), equipmentId);
        if(studentEquipment==null){
            //添加装备，扣除学生金币
            //添加装备
            studentEquipment=new StudentEquipment()
                    .setEquipmentId(equipmentId)
                    .setIntensificationDegree(0)
                    .setStudentId(student.getId())
                    .setType(2);;
            studentEquipmentMapper.insert(studentEquipment);
            //扣除学生金币规则未有暂不做

        }
        return ServerResponse.createBySuccess();
    }
}
