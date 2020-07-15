package com.zhidejiaoyu.student.business.goldCoinFactory.controller;

import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.goldCoinFactory.service.GoldCoinFactoryService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/goldCoinFactory")
public class GoldCoinFactoryController extends BaseController {

    @Resource
    private GoldCoinFactoryService goldCoinFactoryService;


    @RequestMapping("/getList")
    public Object getList(HttpSession session) {
        return goldCoinFactoryService.getList(session);
    }

    @RequestMapping("index")
    public Object index(HttpSession session) {
        return goldCoinFactoryService.getIndex(session);
    }

    @RequestMapping("/getSatelliteClass")
    public Object getSatelliteClass(HttpSession session) {
        return goldCoinFactoryService.getSatelliteClass(session);
    }
}
