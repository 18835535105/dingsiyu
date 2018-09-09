package com.zhidejiaoyu.common.pojo;

import java.util.Date;

public class OpenUnitLog {
    private Long id;

    private Long studentId;

    private Long currentUnitId;

    private Long nextUnitId;

    private Date createTime;

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

    public Long getCurrentUnitId() {
        return currentUnitId;
    }

    public void setCurrentUnitId(Long currentUnitId) {
        this.currentUnitId = currentUnitId;
    }

    public Long getNextUnitId() {
        return nextUnitId;
    }

    public void setNextUnitId(Long nextUnitId) {
        this.nextUnitId = nextUnitId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}