package com.zhidejiaoyu.student.business.wechat.publicaccount.constant;

import com.zhidejiaoyu.student.business.wechat.util.AccessTokenUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 微信公众号接口api常量
 *
 * @author: wuchenxi
 * @date: 2020/5/14 17:08:08
 */
@Slf4j
public class ApiConstant {

    /**
     * 获取公众号基础支持 access_token 接口
     */
    public static String getAccessTokenApi() {
        return "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + ConfigConstant.APP_ID + "&secret=" + ConfigConstant.SECRET;
    }

    /**
     * 获取授权access_token接口api路径
     */
    public static String getAuthAccessTokenApi(String code) {
        return "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + ConfigConstant.APP_ID + "&secret=" + ConfigConstant.SECRET + "&code=" + code + "&grant_type=authorization_code";
    }

    /**
     * 获取用户信息
     */
    public static String getUserInfoApi(String accessToken, String openId) {
        return "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId + "&lang=zh_CN";
    }

    /**
     * 获取创建菜单接口路径
     *
     * @param accessToken
     * @return
     */
    public static String getCreateMenuApi(String accessToken) {
        return "https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + accessToken;
    }

    /**
     * 跳转到微信web授权页面路径
     *
     * @param redirectUrl 授权结束后进入的页面
     * @return
     */
    public static String getAuthPageApi(String redirectUrl) {
        try {
            return "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + ConfigConstant.APP_ID + "&redirect_uri=" + URLEncoder.encode(redirectUrl, "utf-8") + "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        } catch (UnsupportedEncodingException e) {
            log.error("urlEncoder编码出错！", e);
        }
        return null;
    }

    /**
     * 获取刷新授权access_token api url
     *
     * @param refreshToken
     * @return
     */
    public static String getRefreshAuthAccessTokenApi(String refreshToken) {
        return "https://api.weixin.qq.com/sns/oauth2/refresh_token?appid=" + ConfigConstant.APP_ID + "&grant_type=refresh_token&refresh_token=" + refreshToken;
    }

    /**
     * 获得jsapi_ticket
     *
     * @return
     */
    public static String getJSAPITicket() {
        String publicAccountAccessToken = AccessTokenUtil.getPublicAccountAccessToken();
        return "https://api.weixin.qq.com/cgi-bin/ticket/getticket?access_token=" + publicAccountAccessToken + "&type=jsapi";
    }
}
