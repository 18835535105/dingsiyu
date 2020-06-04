package com.zhidejiaoyu.student.business.wechat.qy.auth.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 企业微信授权获取的用户信息
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:23:23
 * @see <a href="https://work.weixin.qq.com/api/doc/90000/90135/91023">参考文档</a>
 */
@Data
public class UserInfoVO implements Serializable {

    private Integer errcode;
    private String errmsg;
    private String UserId;
    private String DeviceId;
    private String OpenId;
}
