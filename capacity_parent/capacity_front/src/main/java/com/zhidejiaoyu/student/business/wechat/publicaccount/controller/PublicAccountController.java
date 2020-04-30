package com.zhidejiaoyu.student.business.wechat.publicaccount.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.service.PublicAccountService;
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
     * 微信公众号授权，获取用户openId
     *
     * @param request
     */
    @RequestMapping("/authorization")
    public ServerResponse<Object> openid(HttpServletRequest request) {
        return publicAccountService.authorization(request);
    }

    /**
     * 扫描卡片后返回校区海报
     *
     * @param cardName  卡片名
     * @return
     */
    @GetMapping("/getCard")
    public ServerResponse<Object> getCard(String cardName) {
        return publicAccountService.getCard(cardName);
    }
}
