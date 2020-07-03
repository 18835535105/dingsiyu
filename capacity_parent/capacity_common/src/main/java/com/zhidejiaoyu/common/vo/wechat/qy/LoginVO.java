package com.zhidejiaoyu.common.vo.wechat.qy;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信绑定之后响应信息
 *
 * @author: wuchenxi
 * @date: 2020/6/30 09:21:21
 */
@Data
public class LoginVO implements Serializable {

    /**
     * 教师openid
     */
    private String openid;

    /**
     * 教师uuid
     */
    private String uuid;
}
