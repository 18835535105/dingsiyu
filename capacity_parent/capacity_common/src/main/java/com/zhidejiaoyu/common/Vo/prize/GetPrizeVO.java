package com.zhidejiaoyu.common.Vo.prize;

import com.zhidejiaoyu.common.pojo.PrizeExchangeList;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 兑换奖品响应
 *
 * @author: wuchenxi
 * @date: 2020/1/15 10:59:59
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetPrizeVO implements Serializable {

    /**
     * 当前奖品信息
     */
    private PrizeExchangeList prizeExchange;
    /**
     * 剩余金币
     */
    private Integer sysGold;

    /**
     * 提示语
     */
    private String msg;
}
