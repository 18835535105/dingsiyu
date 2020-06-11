package com.zhidejiaoyu.student.business.activity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 每周活动校区排行
 *
 * @author: wuchenxi
 * @date: 2020/5/28 10:23:23
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RankVO implements Serializable {

    /**
     * 参与排行总人数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    private List<RankInfo> rankInfos;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RankInfo {

        private String nickname;

        /**
         * 达成语
         */
        private String complete;

        /**
         * 奖励金币数
         */
        private Integer awardGold;

        /**
         * 当前记录是不是我自己的<ul>
         * <li>true：是我自己</li>
         * <li>false：不是我自己</li>
         * </ul>
         */
        private Boolean me;
    }


}
