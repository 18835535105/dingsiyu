package com.zhidejiaoyu.common.Vo.syntax;

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
    private String title;

    /**
     * 答案
     */
    private String answer;

    /**
     * 语法名
     */
    private String syntaxName;

    /**
     * 语法内容
     */
    private String content;

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
}
