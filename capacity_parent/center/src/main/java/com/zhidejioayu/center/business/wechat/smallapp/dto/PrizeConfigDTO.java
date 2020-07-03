package com.zhidejioayu.center.business.wechat.smallapp.dto;

import lombok.Data;

/**
 * 微信抽奖页面参数
 *
 * @author: wuchenxi
 * @date: 2020/6/24 10:37:37
 */
@Data
public class PrizeConfigDTO {
    private String openId;
    private Long adminId;
    private Long studentId;
    private String weChatimgUrl;
    private String weChatName;
}
