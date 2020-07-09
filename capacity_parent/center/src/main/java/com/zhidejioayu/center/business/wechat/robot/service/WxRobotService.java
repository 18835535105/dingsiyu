package com.zhidejioayu.center.business.wechat.robot.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * @author: wuchenxi
 * @date: 2020/7/7 10:25:25
 */
public interface WxRobotService {

    /**
     * 获取所有学生当天的飞行记录及打卡状态
     *
     * @param account 学生账号，多个账号间用英文,隔开
     * @return
     */
    ServerResponse<Object> getDailyState(String account);
}
