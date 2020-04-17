package com.zhidejiaoyu.student.business.index.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author: wuchenxi
 * @date: 2020/1/14 11:52:52
 */
@Data
public class UnitInfoDTO {

    @NotNull(message = "courseId can't be null!")
    private Long courseId;

    /**
     * 1：单词；2：句型；3：语法；4：课文；5：金币试卷
     */
    @NotNull(message = "type can't be null!")
    private Integer type;
}
