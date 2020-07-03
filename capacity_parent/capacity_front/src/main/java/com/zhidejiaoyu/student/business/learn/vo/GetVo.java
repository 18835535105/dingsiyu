package com.zhidejiaoyu.student.business.learn.vo;

import com.zhidejiaoyu.common.pojo.Student;
import lombok.Data;

@Data
public class GetVo {
    private Student student;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 单词id
     */
    private Long wordId;
    /**
     * 课程id
     */
    private Long courseId;
    /**
     * 是否学对
     */
    private boolean isKnown;
    /**
     * 当前学习数量
     */
    private Integer plan;
    /**
     * 总数量
     */
    private Integer total;
    /**
     * 流程id
     */
    private Long flowId;
    /**
     * 难易成都
     */
    private Integer easyOrHard;
    /**
     * 类型编号
     */
    private Integer type;
    /**
     * 模块名
     */
    private String studyModel;
    /**
     * 类型
     */
    private Integer model;
    /**
     * 错误编号
     */
    private Long[] errorId;

    public void setIsKnown(Boolean isKnown) {
        this.isKnown = isKnown;
    }

    public Boolean getIsKnown() {
        return this.isKnown;
    }

    public Boolean isKnown() {
        return this.isKnown;
    }
}
