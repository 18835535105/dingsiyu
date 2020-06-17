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

    /**
     * 活动排名
     *
     * @param type 1:校区活动排行；2：同服务器排行
     * @return
     */
    ServerResponse<Object> rank(Integer type);

    /**
     * 领取奖励
     *
     * @param awardGold 奖励金币数
     * @return
     */
    ServerResponse<Object> getAward(Integer awardGold);

    /**
     * 统计可领取奖励个数
     *
     * @return
     */
    ServerResponse<Object> getAwardCount();
}
