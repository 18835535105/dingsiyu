package com.zhidejiaoyu.common.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AwardContentType implements Serializable {
    private Integer id;

    private String awardContent;

    private Integer awardGold;

    private Integer totalPlan;

    private String imgUrl;
}
