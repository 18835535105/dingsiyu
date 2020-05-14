package com.zhidejiaoyu.student.business.wechat.publicaccount.constant;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 微信公众号常量
 *
 * @author: wuchenxi
 * @date: 2020/4/28 10:29:29
 */
@Slf4j
@Configuration
public class PublicAccountConstant {

    public static String APP_ID;

    public static String SECRET;

    /**
     * 获取公众号基础支持 access_token 接口
     */
    public static String GET_ACCESS_TOKEN_API;

    @Value("${gongzhonghao.appId}")
    public void setAppId(String appId) {
        APP_ID = appId;
    }

    @Value("${gongzhonghao.secret}")
    public void setSecret(String secret) {
        SECRET = secret;
        setAccessTokenApi();
    }



    public void setAccessTokenApi() {
        GET_ACCESS_TOKEN_API = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + SECRET;
    }


    /**
     * 获取授权access_token接口api路径
     */
    public static String getAuthAccessTokenApiUrl(String code) {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + APP_ID + "&secret=" + SECRET + "&code=" + code + "&grant_type=authorization_code";
    }

    /**
     * 获取用户信息
     */
    public static String getUserInfoApiUrl(String accessToken, String openId) {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
    }

    /**
     * 获取创建菜单接口路径
     *
     * @param accessToken
     * @return
     */
    public static String getCreateMenuApiUrl(String accessToken) {
        return "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
    }

    /**
     * 跳转到微信web授权页面路径
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String getAuthPageApiUrl() {
        String redirectUrl = "https://test.shell.yydz100.com/ec/publicAccount/userInfo/getUserInfo";
        try {
            return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + APP_ID + "&redirect_uri=" + URLEncoder.encode(redirectUrl, "utf-8") + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取刷新授权access_token api url
     *
     * @param refreshToken
     * @return
     */
    public static String getRefreshAuthAccessTokenApiUrl(String refreshToken) {
        return "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + APP_ID + "&grant_type=refresh_token&refresh_token=" + refreshToken;
    }
}
