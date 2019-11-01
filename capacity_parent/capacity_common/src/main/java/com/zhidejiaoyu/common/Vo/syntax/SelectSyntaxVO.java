package com.zhidejiaoyu.common.Vo.syntax;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhidejiaoyu.common.Vo.syntax.game.GameVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 选语法响应数据
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 15:30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SelectSyntaxVO {

    /**
     * 语法id
     */
    private Long id;

    /**
     * 知识点信息
     * key
     * syntaxName：知识点名称
     * content：知识点内容
     */
    private KnowledgePointVO knowledgePoint;

    private GameVO selects;

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
