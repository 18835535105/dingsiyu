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


    /**
     *
     * @param session
     * @param type 1，pk数据 2，飞船挑战
     * @return
     */
    @RequestMapping("/getPKRecord")
    public Object getPKRecord(HttpSession session,int type){
        return shipTestService.getPKRecord(session,type);
    }


    /**
     * 获取单人副本测试
     * @param session
     * @param bossId
     * @return
     */
    @RequestMapping("/getSingleTesting")
    public Object getSingleTesting(HttpSession session,Long bossId){
        return shipTestService.getSingleTesting(session,bossId);
    }

    /**
     *
     * @param session
     * @param bossId
     * @param bloodVolume 血量
     * @return
     */
    @RequestMapping("/saveSingleTesting")
    public Object saveSingleTesting(HttpSession session,Long bossId, Integer bloodVolume){
        return shipTestService.saveSingleTesting(session,bossId,bloodVolume);
    }

}
