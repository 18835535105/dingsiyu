package com.zhidejioayu.center.business.wechat.robot.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.robot.service.WxRobotService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/7/7 10:25:25
 */
@RestController
@RequestMapping("/wechat/robot")
public class WxRobotController {

    @Resource
    private WxRobotService wxRobotService;

    /**
     * 获取所有学生当天的飞行记录及打卡状态
     *
     * @param account 学生账号，多个账号间用英文,隔开
     * @return
     */
    @GetMapping("/getDailyState")
    public ServerResponse<Object> getDailyState(String account) {
        if (StringUtils.isEmpty(account)) {
            return ServerResponse.createByError(400, "account can't be null!");
        }
        return wxRobotService.getDailyState(account);
    }
}
