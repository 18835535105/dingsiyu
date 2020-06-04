package com.zhidejiaoyu.student.business.wechat.qy.auth.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * 企业微信授权、用户信息获取
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:17:17
 */
public interface QyAuthService {

    /**
     * 获取用户openid
     *
     * @return
     */
    ServerResponse<Object> getUserInfo();
}
