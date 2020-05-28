package com.zhidejiaoyu.student.business.activity.service;

import com.zhidejiaoyu.common.pojo.WeekActivity;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.service.BaseService;

/**
 * 每周活动奖励
 *
 * @author: wuchenxi
 * @date: 2020/5/27 14:19:19
 */
public interface ActivityAwardService extends BaseService<WeekActivity> {

    /**
     * 奖励列表数据
     *
     * @return
     */
    ServerResponse<Object> awardList();
}
