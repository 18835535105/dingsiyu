package com.zhidejiaoyu.common.vo;

import lombok.Builder;
import lombok.Data;

@Data
public class GauntletRankVo {

    /**
     * 学生姓名
     */
    private String studentName;
    /**
     * 头像
     */
    private String headUrl;
    /**
     * 点赞
     */
    private Long worship;
    /**
     * 金币
     */
    private Integer gold;
    /**
     * pk值
     */
    private Integer pkNum;
    /**
     * 源分战力
     */
    private Integer sourcePower;
    /**
     * 等级
     */
    private String grade;
    /**
     * 勋章
     */
    private Long medal;
    /**
     * 证书
     */
    private Long ccie;
    /**
     * 地区
     */
    private String address;
    /**
     * 省
     */
    private String province;
    /**
     * 市
     */
    private String city;
    /**
     * 区
     */
    private String area;
}
