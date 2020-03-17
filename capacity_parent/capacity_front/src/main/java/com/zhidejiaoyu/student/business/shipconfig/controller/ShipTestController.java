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
     *
     * @param session
     * @param beChallenged 被挑战人id
     * @return
     */
    @RequestMapping("/getTest")
    public Object getTest(HttpSession session, Long beChallenged) {
        return shipTestService.getTest(session, beChallenged);
    }

    /**
     * 保存挑战记录
     *
     * @param session
     * @param beChallenged 被挑战人id
     * @param type         1挑战成功，2挑战失败
     * @return
     */
    @RequestMapping("/saveTest")
    public Object saveTest(HttpSession session, Long beChallenged, Integer type) {
        return shipTestService.saveTest(session, beChallenged, type);
    }


    @RequestMapping("/getPKRecord")
    public Object getPKRecord(HttpSession session){
        return shipTestService.getPKRecord(session);
    }


}
