package com.zhidejioayu.center.business.wechat.publicaccount.auth.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wuchenxi
 * @date: 2020/5/28 16:31:31
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfigVO implements Serializable {
    /**
     * 公众号的唯一标识
     */
    private String appId;
    /**
     * 生成签名的时间戳
     */
    private Long timestamp;
    /**
     * 生成签名的随机串
     */
    private String nonceStr;
    /**
     * 签名
     */
    private String signature;
}
