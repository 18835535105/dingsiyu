package com.zhidejiaoyu.common.dto.testbeforestudy;

import lombok.Data;

/**
 * 封装年级与单元id的对应关系
 *
 * @author: wuchenxi
 * @date: 2019/12/24 15:36:36
 */
@Data
public class GradeAndUnitIdDTO {

    /**
     * 年级
     */
    private String grade;

    /**
     * 单元id
     */
    private Long unitId;

    /**
     * 年级为非普通形式时，取该字段值作为年级
     */
    private String gradeExt;
}
