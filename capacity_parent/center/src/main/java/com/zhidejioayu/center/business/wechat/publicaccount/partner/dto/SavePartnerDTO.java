package com.zhidejioayu.center.business.wechat.publicaccount.partner.dto;

import lombok.Data;

@Data
public class SavePartnerDTO {

    private String imgUrl;

    private String openId;

    private String nickname;

    /**
     * 商业大佬分数
     */
    private Integer business;

    /**
     * 销售大咖分数
     */
    private Integer sale;

    /**
     * 管理专家分数
     */
    private Integer administration;

    /**
     * 创新
     */
    private Integer innovate;

    /**
     * 初心
     */
    private Integer primordialMind;

    /**
     * 校长
     */
    private Integer principal;

    /**
     * 完美均衡
     */
    private Integer perfectBalance;

    /**
     * 发奋
     */
    private Integer toWorkHard;

    /**
     * 踏实
     */
    private Integer downToEarth;

    /**
     * 全部分数（非前端传参）
     */
    private Integer totalScore;


}
