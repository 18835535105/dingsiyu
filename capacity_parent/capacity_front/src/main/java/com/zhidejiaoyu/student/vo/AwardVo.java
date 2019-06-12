package com.zhidejiaoyu.student.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;

/**
 * 任务奖励页面信息展示
 *
 * @author wuchenxi
 * @date 2018/5/24 16:37
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AwardVo implements Serializable {

    /**
     * 奖励id
     */
    private Long id;

    /**
     * 图标url
     */
    private String imgUrl;

    /**
     * 奖励内容
     */
    private String content;

    /**
     * 奖励金币数
     */
    private Integer gold;

    /**
     * 总进度
     */
    private Integer totalPlan;

    /**
     * 当前进度
     */
    private Double currentPlan;

    /**
     * 是否可领取    true:可领取；false：不可领取
     */
    private Boolean canGet;

    /**
     * 是否已领取    true：已领取；false：未领取
     */
    private Boolean getFlag;
}
