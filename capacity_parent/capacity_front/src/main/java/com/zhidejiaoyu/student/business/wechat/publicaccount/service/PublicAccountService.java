package com.zhidejiaoyu.student.business.wechat.publicaccount.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

import javax.servlet.http.HttpServletRequest;

/**
 * @author: wuchenxi
 * @date: 2020/4/28 10:33:33
 */
public interface PublicAccountService {

    /**
     * 微信公众号授权
     *
     * @param request
     * @return
     */
    ServerResponse<Object> authorization(HttpServletRequest request);
}
