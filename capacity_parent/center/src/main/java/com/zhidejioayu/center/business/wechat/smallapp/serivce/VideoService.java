package com.zhidejioayu.center.business.wechat.smallapp.serivce;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * 小程序视频
 *
 * @author: wuchenxi
 * @date: 2020/4/15 16:53:53
 */
public interface VideoService {

    /**
     * 保存已观看视频信息
     *
     * @param openId
     * @param gold    奖励的金币数
     * @param videoId
     * @return
     */
    ServerResponse<Object> saveVideo(String openId, Integer gold, String videoId);

    /**
     * 获取小程序学习视频
     *
     * @param openId
     * @return
     */
    ServerResponse<Object> getVideo(String openId);
}
