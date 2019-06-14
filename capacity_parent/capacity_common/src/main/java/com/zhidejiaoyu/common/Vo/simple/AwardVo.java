package com.zhidejiaoyu.common.Vo.simple;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 任务奖励页面信息展示
 *
 * @author wuchenxi
 * @date 2018/5/24 16:37
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AwardVo {

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public Integer getTotalPlan() {
        return totalPlan;
    }

    public void setTotalPlan(Integer totalPlan) {
        this.totalPlan = totalPlan;
    }

    public Double getCurrentPlan() {
        return currentPlan;
    }

    public void setCurrentPlan(Double currentPlan) {
        this.currentPlan = currentPlan;
    }

    public Boolean getCanGet() {
        return canGet;
    }

    public void setCanGet(Boolean canGet) {
        this.canGet = canGet;
    }

    public Boolean getGetFlag() {
        return getFlag;
    }

    public void setGetFlag(Boolean getFlag) {
        this.getFlag = getFlag;
    }
}
