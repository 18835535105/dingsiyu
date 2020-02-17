package com.zhidejiaoyu.student.business.smallapp.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 绑定账号dto
 *
 * @author: wuchenxi
 * @date: 2020/2/14 15:39:39
 */
@Data
public class BindAccountDTO {

    /**
     * 学生账号
     */
    @NotEmpty(message = "account can't be null")
    private String account;

    /**
     * 学生密码
     */
    @NotEmpty(message = "password can't be null")
    private String password;
}

