package com.zhidejiaoyu.student.vo.reportvo;

import lombok.Data;

import java.io.Serializable;

/**
 * 学习成果VO
 *
 * @author wuchenxi
 * @date 2018/7/19
 */
@Data
public class LearnResultVO implements Serializable {

    /**
     * 掌握的类型
     * <ul>
     * <li>智慧单词</li>
     * <li>抢分句型</li>
     * <li>原汁原文</li>
     * <li>口语跟读</li>
     * </ul>
     */
    private String type;

    /**
     * 掌握的个数
     */
    private Integer master;

    /**
     * 应掌握的个数
     */
    private Integer shouldMaster;

    /**
     * 掌握率
     */
    private Integer masterRate;

}
