package com.zhidejiaoyu.student.business.wechat.publicaccount.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * @author: wuchenxi
 * @date: 2020/4/28 10:33:33
 */
public interface PublicAccountService {

    /**
     * 微信公众号授权
     *
     * @param code
     * @return
     */
    ServerResponse<Object> getOpenId(String code);

    /**
     * 扫描卡片后返回校区海报
     *
     * @param cardName
     * @return
     */
    ServerResponse<Object> getCard(String cardName);

    /**
     * 获取用户信息
     *
     * @return
     */
    ServerResponse<Object> getUserInfo();
}
