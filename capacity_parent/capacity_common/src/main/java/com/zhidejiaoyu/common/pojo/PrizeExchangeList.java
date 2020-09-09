package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 奖品兑换表
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-21
 */
@TableName("prize_exchange_list")
@Data
@EqualsAndHashCode(callSuper = false)
public class PrizeExchangeList extends Model<PrizeExchangeList> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 奖品名称
     */
    @NotEmpty(message = "奖品名称不能为空！")
    private String prize;
    /**
     * 奖品价格
     */
    @NotNull(message = "兑换价格不能为空！")
    @Min(value = 1, message = "兑换价格不能小于1！")
    @TableField("exchange_prize")
    private Integer exchangePrize;
    /**
     * 奖品数量
     */
    @NotNull(message = "奖品数量不能为空！")
    @Min(value = 1, message = "奖品数量不能小于1！")
    @TableField("total_number")
    private Integer totalNumber;
    /**
     * 奖品剩余数量
     */
    @TableField("surplus_number")
    private Integer surplusNumber;
    /**
     * 奖品图片地址
     */
    @TableField("prize_url")
    private String prizeUrl;
    /**
     * 添加时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 状态
     */
    private Integer state;
    /**
     * 校管id
     */
    @TableField("school_id")
    private Long schoolId;

    @TableField("describes")
    private String describes;

    /**
     * 教师id
     */
    @TableField("teacher_id")
    private Long teacherId;

    @TableField("type")
    private Integer type;

    @TableField(exist = false)
    private String schoolName;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public Integer getExchangePrize() {
        return exchangePrize;
    }

    public void setExchangePrize(Integer exchangePrize) {
        this.exchangePrize = exchangePrize;
    }

    public Integer getTotalNumber() {
        return totalNumber;
    }

    public void setTotalNumber(Integer totalNumber) {
        this.totalNumber = totalNumber;
    }

    public Integer getSurplusNumber() {
        return surplusNumber;
    }

    public void setSurplusNumber(Integer surplusNumber) {
        this.surplusNumber = surplusNumber;
    }

    public String getPrizeUrl() {
        return prizeUrl;
    }

    public void setPrizeUrl(String prizeUrl) {
        this.prizeUrl = prizeUrl;
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

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "PrizeExchangeList{" +
        "id=" + id +
        ", prize=" + prize +
        ", exchangePrize=" + exchangePrize +
        ", totalNumber=" + totalNumber +
        ", surplusNumber=" + surplusNumber +
        ", prizeUrl=" + prizeUrl +
        ", createTime=" + createTime +
        ", state=" + state +
        ", schoolId=" + schoolId +
        "}";
    }
}
