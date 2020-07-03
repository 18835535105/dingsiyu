package com.zhidejioayu.center.business.wechat.smallapp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 藏宝阁查询参数
 *
 * @author: wuchenxi
 * @date: 2020/2/18 17:04:04
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrizeDTO {

    @NotEmpty(message = "openId can't be null")
    private String openId;

    /**
     * 排序字段
     * 1：金币；2：日期
     */
    @NotEmpty(message = "orderField can't be null")
    private String orderField;

    /**
     * 排序方式
     * desc,asc
     */
    @NotEmpty(message = "orderBy can't be null")
    private String orderBy;
}
