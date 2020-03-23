package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 游戏记录表
 * @author wuchenxi
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class GameScore extends Model<GameScore> {
    @TableId(type = IdType.AUTO)
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

    @TableField("`explain`")
    private String explain;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}