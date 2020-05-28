package com.zhidejiaoyu.student.business.activity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 活动列表数据
 *
 * @author: wuchenxi
 * @date: 2020/5/27 17:02:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AwardListVO implements Serializable {

    /**
     * 活动背景图
     */
    private String imgUrl;

    /**
     * 活动名
     */
    private String name;

    /**
     * 活动倒计时
     */
    private Long countDown;

    /**
     * 活动完成列表
     */
    private List<ActivityList> activityList;

    /**
     * 活动完成情况
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityList {

        /**
         * 条件
         */
        private String condition;

        /**
         * 奖励金币数
         */
        private Integer awardGold;

        /**
         * 领取状态
         * <ul>
         *     <li>1：不可领取</li>
         *     <li>2：可领取</li>
         *     <li>3：已领取</li>
         * </ul>
         */
        private Integer canGet;
    }
}

