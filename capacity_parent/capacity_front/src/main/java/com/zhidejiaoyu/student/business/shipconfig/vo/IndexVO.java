package com.zhidejiaoyu.student.business.shipconfig.vo;

import lombok.*;

import java.io.Serializable;
import java.util.List;

/**
 * 飞船配置首页数据
 *
 * @author: wuchenxi
 * @date: 2020/2/27 16:42:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IndexVO implements Serializable {

    private String nickname;

    private String headUrl;

    private Integer gold;

    /**
     * 源分战力值
     */
    private Integer sourcePoser;

    /**
     * 武器信息
     */
    private Info weaponsInfo;

    /**
     * 装甲信息
     */
    private Info armorInfo;

    /**
     * 飞船信息
     */
    private Info shipInfo;

    /**
     * 导弹信息
     */
    private Info missileInfo;

    /**
     * 资源信息
     */
    private Info sourceInfo;

    /**
     * 勋章信息
     */
    private List<Info> medalInfos;

    /**
     * 背景信息
     */
    private Info skinInfo;

    /**
     * 英雄信息
     */
    private Info heroImgInfo;

    /**
     * 各项最大值
     */
    private BaseValue baseValue;

    /**
     * 本周各项状态
     */
    private StateOfWeek stateOfWeek;

    /**
     * 雷达图
     */
    private Radar radar;

    /**
     * 被膜拜总次数
     */
    private Integer byWorshipedCount;

    /**
     * 各个数据信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        private Long id;

        private String url;

        /**
         * 说明
         */
        private String explain;
    }

    /**
     * 各项最大值
     * 最大数值（基础值）=飞船+装备的累积值
     * 说明：各个属性都有各自的基础值，比如说耐久度：飞船耐久度+武器耐久度+装备耐久度+装甲耐久度，其余属性依次类推
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseValue {
        /**
         * 命中率
         */
        private Double hitRate;

        /**
         * 机动力
         */
        private Integer move;

        /**
         * 攻击力
         */
        private Integer attack;

        /**
         * 源分次数
         */
        private Integer source;

        /**
         * 耐久度
         */
        private Integer durability;

        /**
         * 源分攻击
         */
        private Integer sourceAttack;
    }

    /**
     * 本周各项状态
     * 本周状态=攻击百分比=基础值*(20%+学习单词数/30)
     * 说明：
     * 该计算逻辑中的基础值是取各个属性各自的基础值，比如本周攻击状态=攻击基础值*20%+学习单词数/30
     * 学习单词数：最近7天学习的单词数（根据id去重）
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class StateOfWeek extends BaseValue {
    }

    /**
     * 雷达图数据
     */
    @Data
    @EqualsAndHashCode(callSuper = false)
    public static class Radar extends BaseValue {
    }

    /**
     * 小程序雷达图信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode(callSuper = false)
    public static class MyState {

        /**
         * 宠物路径
         */
        private String petUrl;

        /**
         * 飞船名称
         */
        private String shipName;

        /**
         * 雷达图信息
         */
        private Radar radar;

        /**
         * 武器信息
         */
        private Info weaponsInfo;

        /**
         * 装甲信息
         */
        private Info armorInfo;

        /**
         * 飞船信息
         */
        private Info shipInfo;

        /**
         * 导弹信息
         */
        private Info missileInfo;

    }
}
