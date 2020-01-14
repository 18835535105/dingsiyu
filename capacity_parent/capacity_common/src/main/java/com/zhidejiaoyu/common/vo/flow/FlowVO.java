package com.zhidejiaoyu.common.vo.flow;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: wuchenxi
 * @date: 2019/12/26 15:13:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class FlowVO implements Serializable {
    private Long courseId;

    private Long unitId;

    private String courseName;

    private String unitName;

    /**
     * 当前节点名称
     */
    private String modelName;

    /**
     * 流程id
     */
    private Long id;

    /**
     * 宠物名称
     */
    private String petName;

    private String token;

    /**
     * 是否是最后一个单元标识
     * true：是最后一个单元，学习剩余的所有语法课程
     * false：不是最后一个单元，学习当前课程对应的语法课程
     */
     Boolean lastUnit;


    /**
     * 语法游戏英文年级上下册
     */
    private String englishGrade;

    /**
     * 单元序号
     */
    private Integer unitIndex;

    /**
     * 语法年级 年级-上下册
     */
    private String grade;
}
