package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.student.business.smallapp.serivce.SmallProgramTestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/smallApp/test")
public class AppletTestController {

    @Resource
    private SmallProgramTestService smallProgramTestService;


    @RequestMapping("/getTest")
    public Object getTest(HttpSession session, String openId) {
        return smallProgramTestService.getTest(session, openId);
    }

    @RequestMapping("/saveTest")
    public Object saveTest(Integer point, HttpSession session, String openId) {
        return smallProgramTestService.saveTest(point, session, openId);
    }

    @RequestMapping("/getQRCode")
    public Object getQRCode(String openId, String weChatName, String weChatImgUrl) {
        return smallProgramTestService.getQRCode(openId, weChatName, weChatImgUrl);
    }


}
