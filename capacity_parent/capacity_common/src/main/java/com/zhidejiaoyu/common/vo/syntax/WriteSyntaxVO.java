package com.zhidejiaoyu.common.vo.syntax;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 写语法响应数据
 *
 * @author: wuchenxi
 * @Date: 2019/11/1 10:20
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriteSyntaxVO implements Serializable {
    /**
     * 语法id
     */
    private Long id;

    /**
     * 题目
     */
    private TopicVO topic;

    /**
     * 知识点内容
     */
    private KnowledgePointVO knowledgePoint;

    /**
     * 当前单元语法总数
     */
    private Integer total;

    /**
     * 学习进度
     */
    private Integer plan;

    /**
     * 是否是新学习的
     */
    private Boolean studyNew;

    /**
     * 记忆难度
     */
    private Integer memoryDifficult;

    /**
     * 记忆强度
     */
    private Integer memoryStrength;

    /**
     * 是否需要显示首字母
     * 1：需要显示首字母
     * 2：不需要显示首字母
     */
    private Integer model;

    /**
     * 解析
     */
    private String analysis;
}
