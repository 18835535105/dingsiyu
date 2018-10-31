package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生账号回收站，删除的学生信息会存放到该表中
 *
 * @author wuchenxi
 * @date 2018/7/16
 */
public class RecycleBin extends Model<RecycleBin> {
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 删除学生信息的操作人id
     */
    private Long operateUserId;

    /**
     * 删除学生信息的操作人姓名
     */
    private String operateUserName;
    private Long studentId;

    /**
     * 删除状态。1：删除中（默认）；2：已恢复
     */
    private Integer delStatus;

    /**
     * 学生信息删除时间
     */
    private Date createTime;

    /**
     * 学生信息恢复时间
     */
    private Date updateTime;

    /**
     * 恢复学生信息的操作人id
     */
    private Long recoverUserId;

    /**
     * 恢复学生信息的操作人姓名
     */
    private String recoverUserName;

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Long operateUserId) {
        this.operateUserId = operateUserId;
    }


    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getRecoverUserId() {
        return recoverUserId;
    }

    public void setRecoverUserId(Long recoverUserId) {
        this.recoverUserId = recoverUserId;
    }

    public String getRecoverUserName() {
        return recoverUserName;
    }

    public void setRecoverUserName(String recoverUserName) {
        this.recoverUserName = recoverUserName;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
