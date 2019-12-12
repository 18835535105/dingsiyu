package com.zhidejiaoyu.common.Vo.syntax;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *  知识点首页功能数据
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SyntaxCourseVo {
    /**
     * 中文年级
     */
    private String grade;
    /**
     * 英文年级
     */
    private String englishGrade;
    /**
     * 课程id
     */
    private Integer courseId;
    /**
     * 学习模块
     */
    private String model;
    /**
     * 1，正在战斗 2，为战斗 3，已完成
     */
    private int battle;
    /**
     * 计算战斗进度
     */
    private int combatProgress;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 单元名
     */
    private String unitName;
    /**
     * 单元编号
     */
    private Integer unitIndex;


}
