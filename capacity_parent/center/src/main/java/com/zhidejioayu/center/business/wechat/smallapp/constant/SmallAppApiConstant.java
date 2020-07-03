package com.zhidejioayu.center.business.wechat.smallapp.constant;

/**
 * 微信小程序常量类
 *
 * @author: wuchenxi
 * @date: 2020/2/17 09:19:19
 */
public interface SmallAppApiConstant {

    String APP_ID = "wx9059c3fd94cd203a";

    String SECRET = "1648073014c1119045786b4ac71455cd";

    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html">登录凭证校验</a>
     */
    String AUTHORIZATION_API_URL = "https://api.weixin.qq.com/sns/jscode2session?";

    /**
     * 获取 access_token 接口
     */
    String GET_ACCESS_TOKEN_API = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=" + APP_ID + "&secret=" + SECRET;

    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.createQRCode.html">生成小程序码，有个数限制</a>
     */
    String CREATE_AQR_CODE = "https://api.weixin.qq.com/wxa/getwxacode?access_token=";

    /**
     * <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/qr-code/wxacode.getUnlimited.html">生成小程序码，无个数限制</a>
     */
    String GET_UNLIMIT_QR_CODE = "https://api.weixin.qq.com/wxa/getwxacodeunlimit?access_token=";
}
