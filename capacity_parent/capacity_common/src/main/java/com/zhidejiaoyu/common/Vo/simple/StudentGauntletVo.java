package com.zhidejiaoyu.common.Vo.simple;

import lombok.Data;

@Data
public class StudentGauntletVo {


    /**
     * 学生id
     */
    private Long id;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 头像地址
     */
    private String headUrl;

    /**
     * 学生账号
     */
    private String account;

    /**
     * pk场数
     */
    private Integer pkNumber;

    /**
     * 胜率
     */
    private String winner;

    /**
     * 与我交手次数
     */
    private Integer forMe;

    /**
     * 学习力
     */
    private Integer study;

    /**
     * 剩余pk次数
     */
    private Integer pkNum;

    /**
     * 挑战状态
     */
    private Integer status;

    /**
     * 是否为第一次观看
     */
    private boolean first;

    /**
     * 是否查看说明
     */
    private Integer pkExplain;

















}
