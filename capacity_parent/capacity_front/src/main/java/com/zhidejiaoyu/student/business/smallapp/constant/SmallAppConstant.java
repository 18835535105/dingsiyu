package com.zhidejiaoyu.student.business.smallapp.constant;

/**
 * 微信小程序常量类
 *
 * @author: wuchenxi
 * @date: 2020/2/17 09:19:19
 */
public interface SmallAppConstant {

    String APP_ID = "wx991c0b0330281fd1";

    String SECRET = "e512f4eae621183834ba2376a63eaa8e";



    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html">登录凭证校验</a>
     */
    String AUTHORIZATION_API_URL = "https://api.weixin.qq.com/sns/jscode2session?";

}
