package com.zhidejiaoyu.common.pojo;

import java.util.Date;

public class Power {
    private Long id;

    private String powerName;

    private Date createTime;

    private Date updateTiem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPowerName() {
        return powerName;
    }

    public void setPowerName(String powerName) {
        this.powerName = powerName == null ? null : powerName.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTiem() {
        return updateTiem;
    }

    public void setUpdateTiem(Date updateTiem) {
        this.updateTiem = updateTiem;
    }
}