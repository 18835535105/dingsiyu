package com.zhidejiaoyu.common.vo.simple;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class StudentGauntletVo {


    /**
     * 学生id
     */
    private Long id;

    /**
     * 学生姓名
     */
    private String name;

    /**
     * 头像地址
     */
    private String headUrl;

    /**
     * 学生账号
     */
    private String account;

    /**
     * pk场数
     */
    private Integer pkNumber;
    /**
     * 源分战力
     */
    private Integer sourcePower;

    /**
     * 胜率
     */
    private String winner;

    /**
     * 与我交手次数
     */
    private Integer forMe;

    /**
     * 学习力
     */
    private Integer study;

    /**
     * 剩余pk次数
     */
    private Integer pkNum;

    /**
     * 挑战状态
     */
    private Integer status;

    /**
     * 是否为第一次观看
     */
    private boolean first;

    /**
     * 是否查看说明
     */
    private Integer pkExplain;
    /**
     * 飞船信息
     */
    private Info shipInfo;


    /**
     * 各个数据信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Info {
        /**
         * 飞船信息
         */
        private shipName shipInfo;
        /**
         * 装甲信息
         */
        private shipName armorInfo;
        /**
         * 武器信息
         */
        private shipName weaponsInfo;
        /**
         * 导弹信息
         */
        private shipName missileInfo;
        /**
         * 各项最大值
         */
        private BaseValue baseValue;
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
     * 各个数据信息
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class shipName {
        private String name;
        private String imgUrl;
    }


}
