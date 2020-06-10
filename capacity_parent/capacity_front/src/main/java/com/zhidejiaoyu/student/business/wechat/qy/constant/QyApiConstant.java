package com.zhidejiaoyu.student.business.wechat.qy.constant;

import com.zhidejiaoyu.student.business.wechat.util.AccessTokenUtil;

/**
 * 企业微信api
 *
 * @author: wuchenxi
 * @date: 2020/6/4 11:36:36
 */
public class QyApiConstant {

    /**
     * 获取企业微信accessToken
     *
     * @return
     */
    public static String getAccessTokenApi() {
        return "https://qyapi.weixin.qq.com/cgi-bin/gettoken?corpid=" + QyConfigConstant.APP_ID + "&corpsecret=" + QyConfigConstant.SECRET;
    }

    /**
     * 获取访问用户身份
     *
     * @return
     */
    public static String getUserInfoApi(String code) {
        String qyAccessToken = AccessTokenUtil.getQyAccessToken();
        return "https://qyapi.weixin.qq.com/cgi-bin/user/getuserinfo?access_token=" + qyAccessToken + "&code=" + code;
    }

    /**
     * userId 转 openId
     *
     * @return
     * @see <a href="https://work.weixin.qq.com/api/doc/90000/90135/90202">参考链接</a>
     */
    public static String getUserIdToOpenIdApi() {
        String qyAccessToken = AccessTokenUtil.getQyAccessToken();
        return "https://qyapi.weixin.qq.com/cgi-bin/user/convert_to_openid?access_token=" + qyAccessToken;
    }

    /**
     * 获得jsapi_ticket
     *
     * @return
     */
    public static String getJSAPITicket() {
        String qyAccessToken = AccessTokenUtil.getQyAccessToken();
        return "https://qyapi.weixin.qq.com/cgi-bin/get_jsapi_ticket?access_token=" + qyAccessToken;
    }
}
