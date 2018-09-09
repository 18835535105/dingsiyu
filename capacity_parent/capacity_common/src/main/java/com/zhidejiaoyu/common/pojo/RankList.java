package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class RankList implements Serializable {
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
}