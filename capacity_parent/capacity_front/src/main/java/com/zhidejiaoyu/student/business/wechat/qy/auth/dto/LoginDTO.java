package com.zhidejiaoyu.student.business.wechat.qy.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 企业微信登录、绑定参数
 *
 * @author: wuchenxi
 * @date: 2020/6/18 14:37:37
 */
@Data
public class LoginDTO {

    /**
     * 登录成功需要跳转的路径
     */
    @NotNull(message = "redirectUrl can't be null!")
    @NotBlank(message = "redirectUrl can't be null!")
    private String redirectUrl;

    @NotNull(message = "openId can't be null!")
    @NotBlank(message = "openId can't be null!")
    private String openId;

    @NotNull(message = "account can't be null!")
    @NotBlank(message = "account can't be null!")
    private String account;

    @NotNull(message = "password can't be null!")
    @NotBlank(message = "password can't be null!")
    private String password;
}
