package com.zhidejiaoyu.student.business.wechat.qy.auth.controller;

import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.StringUtil;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.student.business.wechat.qy.auth.service.QyAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 企业微信授权、用户信息获取
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:13:13
 */
@Controller
@RequestMapping("/qy/auth")
public class QyAuthController {

    @Value("${qywx.redirect.login}")
    private String loginUrl;

    @Resource
    private QyAuthService qyAuthService;

    /**
     * 网页授权获取用户信息
     * 如果用户已经登录，重定向到业务网站
     * 如果用户还没有登录，重定向到登录网页
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public String getUserInfo() {
        SysUser sysUser = qyAuthService.getUserInfo();

        String url = HttpUtil.getHttpServletRequest().getParameter("url");
        if (StringUtil.isEmpty(sysUser.getAccount())) {
            url = loginUrl;
            return "redirect:" + url + "?openid=" + sysUser.getOpenid() + "&redirect_url=" + url;
        }

        return "redirect:" + url + "?openid=" + sysUser.getOpenid();
    }

}
