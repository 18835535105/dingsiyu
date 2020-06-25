package com.zhidejiaoyu.student.business.wechat.qy.auth.service;

import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.qy.auth.dto.LoginDTO;

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
    SysUser getUserInfo();

    /**
     * 用户登录
     *
     * @param loginDTO
     */
    ServerResponse<Object> login(LoginDTO loginDTO);
}
