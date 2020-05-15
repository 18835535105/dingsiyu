package com.zhidejiaoyu.student.business.wechat.publicaccount.auth.controller;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.vo.UserInfoVO;
import com.zhidejiaoyu.student.business.wechat.publicaccount.auth.service.PublicAccountService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * 微信公众号用户信息
 *
 * @author: wuchenxi
 * @date: 2020/5/14 11:00:00
 */
@Controller
@RequestMapping("/publicAccount/userInfo")
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
    public String getUserInfo() {
        String code = HttpUtil.getHttpServletRequest().getParameter("code");
        ServerResponse<Object> userInfo = publicAccountService.getUserInfo(code);
        UserInfoVO userInfoVO = (UserInfoVO) userInfo.getData();

        return "redirect:" + redirectUrl + "?openId=" + userInfoVO.getOpenid() +
                "&headimgurl=" + userInfoVO.getHeadimgurl() +
                "&nickname=" + userInfoVO.getNickname();
    }
}
