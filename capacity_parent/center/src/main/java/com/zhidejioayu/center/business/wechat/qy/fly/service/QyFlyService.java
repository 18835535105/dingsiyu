package com.zhidejioayu.center.business.wechat.qy.fly.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * @author: wuchenxi
 * @date: 2020/6/16 13:54:54
 */
public interface QyFlyService extends IService<CurrentDayOfStudy> {

    ServerResponse<Object> getCurrentDayOfStudy(String studentUuid);
}
