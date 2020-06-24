package com.zhidejioayu.center.business.wechat.publicaccount.auth.controller;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.business.wechat.publicaccount.auth.service.PublicAccountService;
import com.zhidejioayu.center.business.wechat.publicaccount.auth.vo.UserInfoVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    @Value("${gongzhonghao.redirect.partner}")
    private String redirectUrl;

    @Resource
    private PublicAccountService publicAccountService;

    /**
     * 微信公众号授权后跳转到合伙人测试页面
     *
     * @return
     */
    @GetMapping("/getUserInfo")
    public void getUserInfo(HttpServletResponse response) throws IOException {
        String code = HttpUtil.getHttpServletRequest().getParameter("code");
        ServerResponse<Object> userInfo = publicAccountService.getUserInfo(code);
        UserInfoVO userInfoVO = (UserInfoVO) userInfo.getData();

        String url = redirectUrl + "?openId=" + userInfoVO.getOpenid() +
                "&headimgurl=" + userInfoVO.getHeadimgurl() +
                "&nickname=" + userInfoVO.getNickname() +
                "&blank=blank";
        response.sendRedirect(url);
    }
}
