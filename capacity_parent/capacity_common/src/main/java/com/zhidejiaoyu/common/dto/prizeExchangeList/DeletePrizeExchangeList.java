package com.zhidejiaoyu.common.dto.prizeExchangeList;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class DeletePrizeExchangeList {
    @NotNull(message = "openId can't be null!")
    private String openId;
    private List<Integer> prizeIds;
    private Long prizeId;
}
