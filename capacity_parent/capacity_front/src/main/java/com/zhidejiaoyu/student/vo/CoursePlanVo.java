package com.zhidejiaoyu.student.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 课程进度弹框显示内容
 *
 * @author wuchenxi
 * @date 2018/5/22 14:55
 */
@Data
public class CoursePlanVo implements Serializable {

    /**
     * 模块名
     */
    private String studyModel;

    /**
     * 进度
     */
    private Double plan;

    /**
     * 已学量
     */
    private Integer learned;

    /**
     * 总量
     */
    private Integer total;

    /**
     * 待复习量（达到黄金记忆点的量）
     */
    private Integer needReview;
}
