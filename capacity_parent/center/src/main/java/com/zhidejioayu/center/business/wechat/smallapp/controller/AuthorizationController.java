package com.zhidejioayu.center.business.wechat.smallapp.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.smallapp.dto.BindAccountDTO;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.AuthorizationService;
import com.zhidejioayu.center.business.wechat.smallapp.serivce.IndexService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 授权与绑定接口
 *
 * @author: wuchenxi
 * @date: 2020/2/17 09:22:22
 */
@Slf4j
@RestController
@RequestMapping("/wechat/smallApp/authorization")
public class AuthorizationController {

    @Resource
    private AuthorizationService authorizationService;

    @Resource
    private IndexService indexService;

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
     * 微信授权
     *
     * @return
     */
    @RequestMapping("/authorization")
    public ServerResponse<Object> authorization(HttpServletRequest request) {
        return authorizationService.authorization(request);
    }

    /**
     * 绑定队长账号
     *
     * @param dto
     * @return
     */
    @PostMapping("/bind")
    public ServerResponse<Object> bind(@Valid BindAccountDTO dto, BindingResult result) {
        return authorizationService.bind(dto);
    }

    @PostMapping("/unbundling")
    public ServerResponse<Object>  Unbundling( String openId){
        return authorizationService.unbundling(openId);
    }

}
