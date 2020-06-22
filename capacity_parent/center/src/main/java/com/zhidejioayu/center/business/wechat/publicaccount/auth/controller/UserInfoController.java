package com.zhidejioayu.center.business.wechat.publicaccount.auth.controller;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author: wuchenxi
 * @date: 2020/6/22 14:28:28
 */
@Controller
@RequestMapping("/wechat/publicAccount/userInfo")
public class UserInfoController {

    @Resource
    private RestTemplate restTemplate;

    /**
     * 微信公众号授权后跳转到合伙人测试页面
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public void getUserInfo(HttpServletResponse response) throws IOException {

        String code = HttpUtil.getHttpServletRequest().getParameter("code");

        String uri = PublicAccountController.apiUrl + "/userInfo/getUserInfo?code=" + code;
        ResponseEntity<ServerResponse> forEntity = restTemplate.getForEntity(uri, ServerResponse.class);

        response.sendRedirect(forEntity.getBody().toString());
    }
}
