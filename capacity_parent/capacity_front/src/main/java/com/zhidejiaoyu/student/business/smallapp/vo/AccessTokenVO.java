package com.zhidejiaoyu.student.business.smallapp.vo;

import lombok.Data;

/**
 * accessToken
 *
 * @author: wuchenxi
 * @date: 2020/2/20 09:42:42
 */
@Data
public class AccessTokenVO {
    /**
     * 获取到的凭证
     */
    private String access_token;

    /**
     * 凭证有效时间，单位：秒
     */
    private Long expires_in;
}