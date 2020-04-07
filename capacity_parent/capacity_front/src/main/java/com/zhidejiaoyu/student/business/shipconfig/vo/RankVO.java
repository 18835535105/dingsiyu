package com.zhidejiaoyu.student.business.shipconfig.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 排行信息
 *
 * @author: wuchenxi
 * @date: 2020/2/28 10:48:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankVO implements Serializable {

    private List<RankInfo> rankInfoList;

    /**
     * 我的排名
     */
    private Long myRank;

    /**
     * 参与排行总人数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RankInfo {

        private Long studentId;

        private String nickName;

        /**
         * 源分战力值
         */
        private Integer sourcePower;

        private String headUrl;

        /**
         * 是否可以被膜拜
         */
        private Boolean canWorship;
    }
}
