package com.zhidejiaoyu.common.Vo.syntax;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 学语法响应数据
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 09:52
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LearnSyntaxVO implements Serializable {

    /**
     * 语法id
     */
    private Long id;

    /**
     * 语法内容
     */
    private KnowledgePointVO content;

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
