package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class RankList extends Model<RankList> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Integer schoolDayRank;

    private Integer schoolWeekRank;

    private Integer schoolMonthRank;

    private Integer schoolLowestRank;

    private Integer countryWeekRank;

    /**
     * 全国日排行
     */
    private Integer countryDayRank;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}