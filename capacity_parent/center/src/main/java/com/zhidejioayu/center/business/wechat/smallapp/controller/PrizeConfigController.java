package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.PrizeConfigDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.PrizeConfigService;
import com.zhidejioayu.center.business.wechat.smallapp.vo.ReturnAdminVo;
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
@RequestMapping("/wechat/smallApp/prizeConfig")
public class PrizeConfigController {

    @Resource
    private PrizeConfigService prizeConfigService;

    @RequestMapping("/getPrizeConfig")
    public Object getPrize(PrizeConfigDTO dto) {
        return prizeConfigService.getPrizeConfig(dto);
    }


    @RequestMapping("/getAdmin")
    public ServerResponse<ReturnAdminVo> getAdmin(String openId) {
        if (openId == null || openId.equals("")) {
            return ServerResponse.createByError(400, "请传入openId");
        }
        return prizeConfigService.getAdmin(openId);
    }

}
