package com.zhidejiaoyu.student.business.timingtask.controller;


import com.zhidejiaoyu.student.business.timingtask.service.QuartzShipService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RequestMapping("/quart/ship")
@RestController
public class QuartShipController {


    @Resource
    private QuartzShipService quartzShipService;


    @RequestMapping("/week")
    public Object week(){
        quartzShipService.weekUnclock();
        quartzShipService.totalUnclock();
        return null;
    }




}
