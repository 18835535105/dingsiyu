package com.zhidejiaoyu.student.business.wechat.publicaccount.constant;

/**
 * 微信公众号常量
 *
 * @author: wuchenxi
 * @date: 2020/4/28 10:29:29
 */
public interface PublicAccountConstant {
    String APP_ID = "wx991c0b0330281fd1";

    String SECRET = "e512f4eae621183834ba2376a63eaa8e";

    /**
     * 获取 access_token 接口
     */
    String GET_ACCESS_TOKEN_API = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + PublicAccountConstant.APP_ID + "&secret=" + PublicAccountConstant.SECRET;

    /**
     * 授权接口
     */
    String AUTHORIZATION_API_URL = "https://api.weixin.qq.com/sns/oauth2/access_token?";
}
