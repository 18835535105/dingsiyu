package com.zhidejiaoyu.student.business.wechat.smallapp.dto;

import lombok.Data;

/**
 * 授权响应dto
 *
 * <a href="https://developers.weixin.qq.com/miniprogram/dev/api-backend/open-api/login/auth.code2Session.html">参考文档</a>
 *
 * @author: wuchenxi
 * @date: 2020/2/17 09:48:48
 */
@Data
public class AuthorizationDTO {

    /**
     * 用户唯一标识
     */
    private String openid;

    /**
     * 会话密钥
     */
    private String session_key;

    /**
     * 用户在开放平台的唯一标识符，在满足 UnionID 下发条件的情况下会返回
     */
    private String unionid;

    /**
     * 错误码
     */
    private Integer errcode;

    /**
     * 错误信息
     */
    private String errmsg;
}
