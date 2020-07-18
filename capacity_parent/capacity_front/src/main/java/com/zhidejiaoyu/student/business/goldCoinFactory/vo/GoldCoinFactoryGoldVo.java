package com.zhidejiaoyu.student.business.goldCoinFactory.vo;

import lombok.Data;

@Data
public class GoldCoinFactoryGoldVo {
    private Long time;
    private Integer gold;
    private String deadline;
    private Integer satelliteClass;
    private Integer nextSatelliteClass;
    private Integer studentGold;
    private Integer nextSatelliteClassGold;
}

