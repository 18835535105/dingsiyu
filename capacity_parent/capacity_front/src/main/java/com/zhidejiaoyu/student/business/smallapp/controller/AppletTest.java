package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/smallApp/test")
public class AppletTest {

    @Resource
    private SmallProgramTestService smallProgramTestService;


    @RequestMapping("getTest")
    public Object getTest(HttpSession session){
        return smallProgramTestService.getTest(session);
    }

    @RequestMapping("saveTest")
    public Object saveTest(Integer point ,HttpSession session){
        return smallProgramTestService.saveTest(point,session);
    }







}
