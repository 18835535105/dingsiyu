package com.zhidejiaoyu.student.business.wechat.qy.auth.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.qy.auth.service.QyAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 企业微信授权、用户信息获取
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:13:13
 */
@RestController
@RequestMapping("/qy/auth")
public class QyAuthController {

    @Resource
    private QyAuthService qyAuthService;

    /**
     * 网页授权获取用户信息
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public ServerResponse<Object> getUserInfo() {
        return qyAuthService.getUserInfo();
    }

}
