package com.zhidejiaoyu.common.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TeksTestResultVo {

    private String petName;

    private String text;

    private String imgUrl;

    /**
     * 分数
     */
    private Integer point;

    /**
     * 奖励金币数
     */
    private Integer gold;

    /**
     * 测试结果页弹框显示的提示语
     */
    private String msg;

    /**
     * 测试结果页背景上的提示语
     */
    private String backMsg;

    /**
     * 宠物图片url
     */
    private String petUrl;

    /**
     * 宠物提示语
     */
    private String petSay;

    /**
     * 奖励能量数
     */
    private Integer energy;
}
