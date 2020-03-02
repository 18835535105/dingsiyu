package com.zhidejiaoyu.student.business.shipconfig.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    /**
     * 源分战力值
     */
    private Integer sourcePoser;

    /**
     * 武器图片路径
     */
    private String weaponsUrl;

    /**
     * 装甲图片路径
     */
    private String armorUrl;

    /**
     * 飞船图片路径
     */
    private String shipUrl;

    /**
     * 导弹图片路径
     */
    private String missileUrl;

    /**
     * 资源图片路径
     */
    private String sourceUrl;

    /**
     * 勋章图片地址
     */
    private List<String> medalUrl;

    /**
     * 背景图片地址
     */
    private String skinImgUrl;

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
         * 源力
         */
        private Integer source;

        /**
         * 耐久度
         */
        private Integer durability;

        /**
         * 源力攻击
         */
        private transient Integer sourceAttack;
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

}
