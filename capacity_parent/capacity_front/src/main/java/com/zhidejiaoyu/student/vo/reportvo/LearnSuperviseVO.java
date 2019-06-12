package com.zhidejiaoyu.student.vo.reportvo;

import lombok.Data;

import java.io.Serializable;

/**
 * 学习监督VO
 * @author wuchenxi
 * @date 2018/7/20
 */
@Data
public class LearnSuperviseVO implements Serializable {

    /**
     * 已学单词总个数
     */
    private Integer wordCount;

    /**
     * 已学例句总个数
     */
    private Integer sentenceCount;

    /**
     * 已学课文总个数
     */
    private Integer textCount;

    /**
     * 已学口语总个数
     */
    private Integer spokenCount;
    /**
     * 已学知识点总数量
     */
    private Integer learnedCount;

    /**
     * 战胜学生的百分比
     */
    private Integer defeatRate;

    /**
     * 总在线时长（小时)
     */
    private Integer totalOnlineTime;

    /**
     * 总有效时长(小时)
     */
    private Integer totalValidTime;

    /**
     * 学习效率
     */
    private Integer efficiency;
}
