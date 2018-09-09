package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class AwardContentType implements Serializable {
    private Integer id;

    private String awardContent;

    private Integer awardGold;

    private Integer totalPlan;

    private String imgUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAwardContent() {
        return awardContent;
    }

    public void setAwardContent(String awardContent) {
        this.awardContent = awardContent == null ? null : awardContent.trim();
    }

    public Integer getAwardGold() {
        return awardGold;
    }

    public void setAwardGold(Integer awardGold) {
        this.awardGold = awardGold;
    }

    public Integer getTotalPlan() {
        return totalPlan;
    }

    public void setTotalPlan(Integer totalPlan) {
        this.totalPlan = totalPlan;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl == null ? null : imgUrl.trim();
    }
}