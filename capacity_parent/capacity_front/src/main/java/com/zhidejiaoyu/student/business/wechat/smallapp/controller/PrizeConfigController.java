package com.zhidejiaoyu.student.business.wechat.smallapp.controller;

import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.wechat.smallapp.serivce.PrizeConfigService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

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
    public Object getPrize(String openId, Long adminId, Long studentId,String weChatimgUrl,String weChatName) {
        return prizeConfigService.getPrizeConfig(openId, adminId, studentId,weChatimgUrl,weChatName);
    }


    @RequestMapping("/getAdmin")
    public Object getAdmin(String openId) {
        return prizeConfigService.getAdmin(openId);
    }

}