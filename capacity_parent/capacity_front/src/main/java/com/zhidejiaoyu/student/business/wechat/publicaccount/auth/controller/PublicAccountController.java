package com.zhidejiaoyu.student.business.wechat.publicaccount.auth.controller;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejiaoyu.student.business.wechat.publicaccount.constant.ConfigConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 微信公众号
 *
 * @author: wuchenxi
 * @date: 2020/4/28 10:32:32
 */
@Slf4j
@RestController
@RequestMapping("/publicAccount")
public class PublicAccountController {

    @Resource
    private PublicAccountService publicAccountService;

    /**
     * 用于验证公众号填写的 url 及 token
     *
     * @param signature
     * @param timestamp
     * @param nonce
     * @param echostr
     * @return
     */
    @GetMapping("/check")
    public Object check(@RequestParam(name = "signature", required = false) String signature,
                        @RequestParam(name = "timestamp", required = false) String timestamp,
                        @RequestParam(name = "nonce", required = false) String nonce,
                        @RequestParam(name = "echostr", required = false) String echostr) {
        return echostr;
    }

    /**
     * 获取JS-SDK配置数据
     *
     * @param url 当前页面路径
     * @return
     */
    @GetMapping("/getConfig")
    public ServerResponse<Object> getConfig(String url) {
        return publicAccountService.getConfig(url, ConfigConstant.APP_ID);
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
