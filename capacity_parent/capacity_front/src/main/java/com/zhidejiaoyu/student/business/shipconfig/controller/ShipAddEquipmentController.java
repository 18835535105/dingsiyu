package com.zhidejiaoyu.student.business.shipconfig.controller;

import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 添加装备
 */
@RestController
@RequestMapping("/shipAddEquipment")
public class ShipAddEquipmentController {

    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;

    @RequestMapping("/addStudentEquipment")
    public Object addStudentEquipment(HttpSession session,Long equipmentId){
       return shipAddEquipmentService.addStudentEquipment(session,equipmentId);
    }





}
