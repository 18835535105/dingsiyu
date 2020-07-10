package com.zhidejioayu.center.business.wechat.qy.auth.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.qy.auth.service.QyAuthService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 企业微信授权、用户信息获取
 *
 * <a href="https://www.showdoc.cc/dfdzcenter?page_id=4793315288477743">授权逻辑</a>
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:13:13
 */
@Validated
@Controller
@RequestMapping("/wechat/qy/auth")
public class QyAuthController {

    @Value("${qywx.authLink}")
    private String authLink;

    @Value("${qywx.redirect.login}")
    private String loginUrl;

    @Resource
    private QyAuthService qyAuthService;

    /**
     * 网页授权，保存用户openid和姓名
     *
     * @return
     */
    @GetMapping("/auth")
    public void auth(HttpServletResponse response) throws IOException {
        qyAuthService.auth();
        response.sendRedirect(loginUrl + "/#/?state=3");
    }

    /**
     * 验证授权状态
     *
     * @param response
     * @throws IOException
     */
    @GetMapping("/auth/state")
    public void authState(HttpServletResponse response) throws IOException {
        int state = qyAuthService.authState();
        response.sendRedirect(loginUrl + "/#/?state=" + state);
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

    /**
     * 获取授权链接url
     *
     * @return
     */
    @ResponseBody
    @GetMapping("/getAuthUrl")
    public ServerResponse<Object> getAuthUrl() {
        Map<String, String> map = new HashMap<>(16);
        map.put("url", authLink);
        return ServerResponse.createBySuccess(map);
    }

}
