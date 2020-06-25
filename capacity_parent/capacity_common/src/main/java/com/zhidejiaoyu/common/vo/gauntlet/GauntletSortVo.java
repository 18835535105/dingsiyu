package com.zhidejiaoyu.common.vo.gauntlet;

import lombok.Data;

@Data
public class GauntletSortVo {
    /**
     * 战斗场次排序
     */
    private Integer battle;
    /**
     * pk值
     */
    private Integer pkNum;
    /**
     * 源分战力
     */
    private Integer sourcePower;
}
