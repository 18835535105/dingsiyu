package com.zhidejiaoyu.student.business.timingtask.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzRobotService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 微信对话机器人定时任务调用接口
 *
 * @author: wuchenxi
 * @date: 2020/4/7 17:55:55
 */
@RestController
@RequestMapping("/quartz/robot")
public class QuartzRobotController {

    @Resource
    private QuartzRobotService quartzRobotService;

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
        return quartzRobotService.getDailyState(account);
    }
}
