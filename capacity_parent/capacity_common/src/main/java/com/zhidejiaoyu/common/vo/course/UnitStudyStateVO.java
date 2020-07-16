package com.zhidejiaoyu.common.vo.course;

import lombok.Data;

import java.io.Serializable;

/**
 * 单元学习、测试状态
 *
 * @author wuchenxi
 * @date 2020-07-16 15:18:50
 */
@Data
public class UnitStudyStateVO implements Serializable {

    private String unitName;

    private Long unitId;

    /**
     * 简单流程学习状态
     * <ul>
     *     <li>1：已完成</li>
     *     <li>2：进行中</li>
     *     <li>3：未开始</li>
     * </ul>
     */
    private Integer easyState;

    /**
     * 难流程学习状态
     * <ul>
     *          <li>1：已完成</li>
     *          <li>2：进行中</li>
     *          <li>3：未开始</li>
     *      </ul>
     */
    private Integer hardState;
}
