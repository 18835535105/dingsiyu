package com.zhidejiaoyu.common.dto.prizeExchangeList;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class PrizeExchangeListDto {

    /**
     * 教师openId
     */
    @NotNull(message = "openId can't be null!")
    private String openId;

    private Integer pageNum;

    private Integer pageSize;

}
