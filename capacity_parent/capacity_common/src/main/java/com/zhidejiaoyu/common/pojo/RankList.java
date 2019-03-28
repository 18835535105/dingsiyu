package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
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