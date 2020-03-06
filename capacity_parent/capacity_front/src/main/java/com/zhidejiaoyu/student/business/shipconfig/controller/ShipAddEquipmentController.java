package com.zhidejiaoyu.student.business.shipconfig.controller;

import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 装备界面
 */
@RestController
@RequestMapping("/shipAddEquipment")
public class ShipAddEquipmentController {

    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;

    /**
     * 判断是否解锁数据
     *
     * @param session
     * @return
     */
    @RequestMapping("/queryAddStudentEquipment")
    public Object addStudentEquipment(HttpSession session) {
        return shipAddEquipmentService.queryAddStudentEquipment(session);
    }

    /**
     * 强化装备
     * @param session
     * @param equipmentId
     * @return
     */
    @PostMapping("/strengthenStudentEquipment")
    public Object strengthenStudentEquipment(HttpSession session, Long equipmentId) {
        return shipAddEquipmentService.strengthenStudentEquipment(session, equipmentId);
    }

    /**
     * 装备界面
     */
    @GetMapping("/getEquipmentInterface")
    public Object getEquipmentInterface(HttpSession session,Integer type){
        return shipAddEquipmentService.getEquipmentInterface(session,type);
    }

    /**
     * 穿戴装备
     */
    @PostMapping("/WearEquipment")
    public Object wearEquipment(HttpSession session, Long equipmentId){
        return shipAddEquipmentService.wearEquipment(session,equipmentId);
    }


}
