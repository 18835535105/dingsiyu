package com.zhidejiaoyu.common.dto.prizeExchangeList;

import lombok.Data;

@Data
public class UpdateStudentExchangePrizeDto {
    private Long prizeId;

    /**
     * 兑换状态 	0，成功 3失败
     */
    private Integer state;
    private String openId;
}
