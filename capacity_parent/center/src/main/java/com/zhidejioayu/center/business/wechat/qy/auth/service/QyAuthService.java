package com.zhidejioayu.center.business.wechat.qy.auth.service;

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
    void auth();

    /**
     * 获取用户需要跳转的url
     *
     * @return
     */
    String getRedirectUrl();
}
