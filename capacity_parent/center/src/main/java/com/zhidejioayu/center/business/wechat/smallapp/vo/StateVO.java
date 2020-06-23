package com.zhidejioayu.center.business.wechat.smallapp.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 飞船状态数据
 *
 * @author: wuchenxi
 * @date: 2020/2/18 15:29:29
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StateVO implements Serializable {
    /**
     * 耐久度，在线时长（秒）
     */
    private Integer onlineTime;

    /**
     * 学习单词数
     */
    private Integer wordLearnedCount;

    /**
     * 成绩平均分
     */
    private Double score;

    /**
     * 学习效率（小数）
     */
    private Double efficiency;

    /**
     * 复习次数（执行飞行任务的次数）
     */
    private Integer reviewCount;
}
