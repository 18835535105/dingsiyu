package com.zhidejiaoyu.student.business.smallapp.controller;

import com.zhidejiaoyu.student.business.controller.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 微信抽奖页面
 *
 * @author: liumaoyu
 * @date: 2020/2/14 15:42:00
 */
@RestController
@RequestMapping("/prizeConfig")
public class PrizeConfigController extends BaseController {



    @RequestMapping("/getPrizeConfig")
    public Object getPrize(String weiXinId){
        return null;
    }

}
