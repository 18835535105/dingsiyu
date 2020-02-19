package com.zhidejiaoyu.student.business.smallapp.constant;

/**
 * 微信小程序常量类
 *
 * @author: wuchenxi
 * @date: 2020/2/17 09:19:19
 */
public interface SmallAppApiConstant {

    String APP_ID = "wx58002e9f7162ef3c";

    String SECRET = "2b44fe40ad7e5a63ad015aa205dfce44";

    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html">登录凭证校验</a>
     */
    String AUTHORIZATION_API_URL = "https://api.weixin.qq.com/sns/jscode2session?";

}
