package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author stylefeng
 * @since 2019-02-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
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

    private Date updateTime;

}
