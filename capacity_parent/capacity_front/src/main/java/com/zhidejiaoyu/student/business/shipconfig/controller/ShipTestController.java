package com.zhidejiaoyu.student.business.shipconfig.controller;

import com.zhidejiaoyu.student.business.shipconfig.service.ShipTestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/shipTest")
public class ShipTestController {

    @Resource
    private ShipTestService shipTestService;


    /**
     * 获取pk题目
     * @param session
     * @param studentId
     * @return
     */
    @RequestMapping("/getTest")
    public Object getTest(HttpSession session,Long studentId){
        return shipTestService.getTest(session,studentId);
    }




}
