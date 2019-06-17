package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 金币变化日志
 * </p>
 *
 * @author zdjy
 * @since 2018-10-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("gold_log")
public class GoldLog extends Model<GoldLog> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 操作人id
     */
    @TableField("operator_id")
    private Integer operatorId;
    /**
     * 学生id
     */
    @TableField("student_id")
    private Long studentId;
    /**
     * 增加金币数
     */
    @TableField("gold_add")
    private Integer goldAdd;
    /**
     * 减少金币数
     */
    @TableField("gold_reduce")
    private Integer goldReduce;
    /**
     * 金币变化原因
     */
    private String reason;
    /**
     * 日志生成时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 学生是否已经查阅金币变动：0 未查阅，1 已查阅，2 无需查阅
     */
    @TableField("read_flag")
    private Integer readFlag;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}