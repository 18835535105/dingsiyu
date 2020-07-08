package com.zhidejioayu.center.business.wechat.qy.auth.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.qy.auth.service.QyAuthService;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 企业微信授权、用户信息获取
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:13:13
 */
@Validated
@Controller
@RequestMapping("/wechat/qy/auth")
public class QyAuthController {

    @Resource
    private QyAuthService qyAuthService;

    /**
     * 网页授权，保存用户openid和姓名
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/auth")
    public ServerResponse<Object> auth() {
        qyAuthService.auth();
        return ServerResponse.createBySuccess();
    }

    /**
     * 网页授权，跳转到目标url
     * 如果用户还没有点击过授权链接，跳转到授权链接页面
     * 如果用户已经点击过授权链接，跳转到目标页面
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public void getUserInfo(HttpServletResponse response) throws IOException {
        String url = qyAuthService.getRedirectUrl();
        response.sendRedirect(url);
    }

}
