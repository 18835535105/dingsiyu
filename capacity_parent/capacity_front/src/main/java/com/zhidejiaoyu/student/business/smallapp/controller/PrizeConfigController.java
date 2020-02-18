package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.smallapp.serivce.PrizeConfigService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

/**
 * 微信抽奖页面
 *
 * @author: liumaoyu
 * @date: 2020/2/14 15:42:00
 */
@RestController
@RequestMapping("/smallApp/prizeConfig")
public class PrizeConfigController extends BaseController {

    @Resource
    private PrizeConfigService prizeConfigService;

    @RequestMapping("/getPrizeConfig")
    public Object getPrize(String openId, Long adminId, Long studentId) {
        return prizeConfigService.getPrizeConfig(openId, adminId, studentId);
    }


    @RequestMapping("/getAdmin")
    public Object getAdmin(Long studentId) {
        return prizeConfigService.getAdmin(studentId);
    }

}
