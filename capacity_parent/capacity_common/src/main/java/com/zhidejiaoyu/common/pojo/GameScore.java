package com.zhidejiaoyu.common.pojo;

import lombok.Data;

import java.util.Date;

/**
 * 游戏记录表
 * @author wuchenxi
 */
@Data
public class GameScore {
    private Long id;

    private Long studentId;

    private Long gameId;

    private String gameName;

    private Date gameStartTime;

    private Date gameEndTime;

    private Integer score;

    private Integer awardGold;

    /**
     * 游戏是否通过 0：未通过；1：通过
     */
    private Integer passFlag;

    private String explain;
}