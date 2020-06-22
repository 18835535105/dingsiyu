package com.zhidejioayu.center.business.wechat.publicaccount.auth.controller;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: wuchenxi
 * @date: 2020/6/22 14:27:27
 */
@Slf4j
@RestController
@RequestMapping("/wechat/publicAccount/auth")
public class PublicAccountController {

    protected static String apiUrl = "https://test.yydz100.com/ec/publicAccount";

    @Resource
    private RestTemplate restTemplate;

    /**
     * 获取JS-SDK配置数据
     *
     * @param url 当前页面路径
     * @return
     */
    @GetMapping("/getConfig")
    public ServerResponse<Object> getConfig(String url) {
        String uri = apiUrl + "/getConfig?url=" + url;
        ResponseEntity<ServerResponse> forEntity = restTemplate.getForEntity(uri, ServerResponse.class);
        return forEntity.getBody();
    }

    /**
     * 获取用户openId
     *
     * @param request
     */
    @RequestMapping("/getOpenId")
    public ServerResponse<Object> openid(HttpServletRequest request) {
        String code = request.getParameter("code");
        String uri = apiUrl + "/getOpenId?code=" + code;
        ResponseEntity<ServerResponse> forEntity = restTemplate.getForEntity(uri, ServerResponse.class);
        return forEntity.getBody();
    }

    /**
     * 扫描卡片后返回校区海报
     *
     * @param cardName 卡片名
     * @return
     */
    @GetMapping("/getCard")
    public ServerResponse<Object> getCard(String cardName) {
        String uri = apiUrl + "/getCard?cardName=" + cardName;
        ResponseEntity<ServerResponse> forEntity = restTemplate.getForEntity(uri, ServerResponse.class);
        return forEntity.getBody();
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public ServerResponse<Object> getUserInfo() {
        String code = HttpUtil.getHttpServletRequest().getParameter("code");

        String uri = apiUrl + "/getUserInfo?code=" + code;
        ResponseEntity<ServerResponse> forEntity = restTemplate.getForEntity(uri, ServerResponse.class);
        return forEntity.getBody();
    }

    public static void main(String[] args) {
        System.out.println("so that".equals("so that"));
    }

}
