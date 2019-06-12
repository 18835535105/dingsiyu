package com.zhidejiaoyu.common.Vo.simple;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 记忆追踪vo -简洁版
 *
 * @author qizhentao
 * @version 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SimpleCapacityVo {
    /**
     * 单词等id
     */
    private Long id;

    /**
     * 记忆追踪id
     */
    private Long capacityId;

    /**
     * 学生id
     */
    private Long student_id;
    /**
     * 单元id
     */
    private Long unit_id;
    /**
     * 答错次数
     */
    private int fault_time;
    /**
     * 记忆强度
     */
    private double memory_strength;
    /**
     * 单词
     */
    private String word;
    /**
     * 单词翻译
     */
    private String word_chinese;
    /**
     * 例句
     */
    private String example_english;
    /**
     * 例句翻译
     */
    private String example_chinese;
    /**
     * 单词图片url
     */
    private String recordpicurl;

    /**
     * 是否是新学单词
     */
    private boolean studyNew;
    /**
     * 记忆难度
     */
    private int memoryDifficulty;
    /**
     * 已学
     */
    private Long plan;
    /**
     * 总单词
     */
    private Long wordCount;
    /**
     * 音标
     */
    private String soundMark;
    /**
     * 单词读音url
     */
    private String readUrl;
    /**
     * 词性
     */
    private String exp;

    /**
     * 认知引擎
     */
    private Integer engine;

}
