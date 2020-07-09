package com.zhidejioayu.center.business.wechat.qy.auth.vo;

import lombok.Data;

/**
 * userId与openid互转
 *
 * @author: wuchenxi
 * @date: 2020/6/4 14:35:35
 * @see <a href="https://work.weixin.qq.com/api/doc/90000/90135/90202">参考文档</a>
 */
@Data
public class UserIdToOpenidVO {
    private Integer errcode;
    private String errmsg;
    private String openid;
    private String userid;
}
