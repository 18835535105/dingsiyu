package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;
import java.util.Date;

public class Worship implements Serializable {
    private Long id;

    private Long studentIdWorship;

    private Long studentIdByWorship;

    private Date worshipTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentIdWorship() {
        return studentIdWorship;
    }

    public void setStudentIdWorship(Long studentIdWorship) {
        this.studentIdWorship = studentIdWorship;
    }

    public Long getStudentIdByWorship() {
        return studentIdByWorship;
    }

    public void setStudentIdByWorship(Long studentIdByWorship) {
        this.studentIdByWorship = studentIdByWorship;
    }

    public Date getWorshipTime() {
        return worshipTime;
    }

    public void setWorshipTime(Date worshipTime) {
        this.worshipTime = worshipTime;
    }
}