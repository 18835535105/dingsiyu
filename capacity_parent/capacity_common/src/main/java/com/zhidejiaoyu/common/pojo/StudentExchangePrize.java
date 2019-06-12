package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-25
 */
@TableName("student_exchange_prize")
public class StudentExchangePrize extends Model<StudentExchangePrize> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 奖品列表id
     */
    @TableField("prize_id")
    private Long prizeId;
    /**
     * 学生列表id
     */
    @TableField("student_id")
    private Long studentId;
    /**
     * 学生兑换时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 状态 1,未删除   2，已删除
     */
    private Integer state;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(Long prizeId) {
        this.prizeId = prizeId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "StudentExchangePrize{" +
        "id=" + id +
        ", prizeId=" + prizeId +
        ", studentId=" + studentId +
        ", createTime=" + createTime +
        ", state=" + state +
        "}";
    }
}
