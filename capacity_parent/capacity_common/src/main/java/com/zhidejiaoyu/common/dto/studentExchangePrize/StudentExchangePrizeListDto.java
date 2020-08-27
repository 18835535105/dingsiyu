package com.zhidejiaoyu.common.dto.studentExchangePrize;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class StudentExchangePrizeListDto {

    /**
     * 教师openId
     */
    @NotNull(message = "openId can't be null!")
    private String openId;

    private Integer pageNum;

    private Integer pageSize;

    private String studentName;

}
