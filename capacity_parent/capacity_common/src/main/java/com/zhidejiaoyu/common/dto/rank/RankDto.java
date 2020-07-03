package com.zhidejiaoyu.common.dto.rank;

import lombok.Data;

/**
 * 排行榜参数 dto
 *
 * @author wuchenxi
 * @date 2019-06-21
 */
@Data
public class RankDto {
    /**
     * 第几页
     */
    private Integer page;

    /**
     * 每页数据量
     */
    private Integer rows;

    /**
     * 1：金币
     * 2：勋章
     * 3：证书
     * 4：膜拜
     */
    private Integer type;

    /**
     * 本班排行模块  model = 1
     * 本校模块 model = 2
     * 全国模块 model = 3
     * 同服务器排行 model=4
     */
    private Integer model;

}
