package com.zhidejioayu.center.business.wechat.publicaccount.auth.controller;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejioayu.center.business.wechat.publicaccount.constant.ConfigConstant;
import com.zhidejioayu.center.business.wechat.util.JsApiTicketUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @Resource
    private PublicAccountService publicAccountService;

    /**
     * 获取JS-SDK配置数据
     *
     * @param url 当前页面路径
     * @return
     */
    @GetMapping("/getConfig")
    public ServerResponse<Object> getConfig(String url) {
        return publicAccountService.getConfig(url, ConfigConstant.APP_ID, JsApiTicketUtil.getPublicAccountJsApiTicket());
    }

    /**
     * 获取用户openId
     *
     * @param request
     */
    @RequestMapping("/getOpenId")
    public ServerResponse<Object> openid(HttpServletRequest request) {
        String code = request.getParameter("code");
        return publicAccountService.getOpenId(code);
    }

    /**
     * 扫描卡片后返回校区海报
     *
     * @param cardName 卡片名
     * @return
     */
    @GetMapping("/getCard")
    public ServerResponse<Object> getCard(String cardName) {
        return publicAccountService.getCard(cardName);
    }

    /**
     * 获取用户信息
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public ServerResponse<Object> getUserInfo() {
        String code = HttpUtil.getHttpServletRequest().getParameter("code");
        return publicAccountService.getUserInfo(code);
    }

}
