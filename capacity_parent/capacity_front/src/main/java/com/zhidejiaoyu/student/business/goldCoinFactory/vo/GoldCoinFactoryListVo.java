package com.zhidejiaoyu.student.business.goldCoinFactory.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class GoldCoinFactoryListVo {

    private Long time;
    private Integer gold;
    private List<GoldList> returnList;
    private int size;

    /**
     * 各个数据信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoldList {
        private String studentName;

        private String model;
        /**
         * 说明
         */
        private String studyTime;

        private String getGold;

    }

}

