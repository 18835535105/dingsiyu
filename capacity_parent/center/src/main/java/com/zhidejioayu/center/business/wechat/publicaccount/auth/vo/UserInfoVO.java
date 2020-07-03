package com.zhidejioayu.center.business.wechat.publicaccount.auth.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信公众号用户信息
 *
 * @author: wuchenxi
 * @date: 2020/5/14 10:51:51
 */
@Data
public class UserInfoVO implements Serializable {

    private Integer errcode;

    private String errmsg;

    private String openid;
    private String nickname;

    /**
     * 用户的性别，值为1时是男性，值为2时是女性，值为0时是未知
     */
    private String sex;

    /**
     * 用户头像，最后一个数值代表正方形头像大小（有0、46、64、96、132数值可选，0代表640*640正方形头像），用户没有头像时该项为空。若用户更换头像，原有头像URL将失效。
     */
    private String headimgurl;
    private String unionid;
}
