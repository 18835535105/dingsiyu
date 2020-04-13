package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;
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
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    /**
     * 增减类型；1 学习奖励 2 奖品兑换 3 教师奖励 4 教师处罚 5 补签
     */
    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
