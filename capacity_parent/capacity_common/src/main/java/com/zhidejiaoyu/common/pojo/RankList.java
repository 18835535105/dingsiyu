package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

public class RankList extends Model<RankList> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Integer schoolDayRank;

    private Integer schoolWeekRank;

    private Integer schoolMonthRank;

    private Integer schoolLowestRank;

    private Integer countryWeekRank;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getSchoolDayRank() {
        return schoolDayRank;
    }

    public void setSchoolDayRank(Integer schoolDayRank) {
        this.schoolDayRank = schoolDayRank;
    }

    public Integer getSchoolWeekRank() {
        return schoolWeekRank;
    }

    public void setSchoolWeekRank(Integer schoolWeekRank) {
        this.schoolWeekRank = schoolWeekRank;
    }

    public Integer getSchoolMonthRank() {
        return schoolMonthRank;
    }

    public void setSchoolMonthRank(Integer schoolMonthRank) {
        this.schoolMonthRank = schoolMonthRank;
    }

    public Integer getSchoolLowestRank() {
        return schoolLowestRank;
    }

    public void setSchoolLowestRank(Integer schoolLowestRank) {
        this.schoolLowestRank = schoolLowestRank;
    }

    public Integer getCountryWeekRank() {
        return countryWeekRank;
    }

    public void setCountryWeekRank(Integer countryWeekRank) {
        this.countryWeekRank = countryWeekRank;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}