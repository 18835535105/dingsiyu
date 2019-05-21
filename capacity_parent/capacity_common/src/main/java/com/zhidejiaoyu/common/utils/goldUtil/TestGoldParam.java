package com.zhidejiaoyu.common.utils.goldUtil;

import lombok.Data;

/**
 * 用于计算获取的金币个数所需参数
 *
 * @author wuchenxi
 * @date 2019-05-21
 */
@Data
public class TestGoldParam {

    private Integer point;

    private Long unitId;

    private Integer classify;
}
