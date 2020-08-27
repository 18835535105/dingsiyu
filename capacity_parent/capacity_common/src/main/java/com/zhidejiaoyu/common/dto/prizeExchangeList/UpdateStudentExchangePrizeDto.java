package com.zhidejiaoyu.common.dto.prizeExchangeList;

import lombok.Data;

@Data
public class UpdateStudentExchangePrizeDto {
    private Long prizeId;
    private Integer state;
    private String openId;
}
