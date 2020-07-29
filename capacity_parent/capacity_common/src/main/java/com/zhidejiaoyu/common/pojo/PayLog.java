package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("pay_log")
public class PayLog extends Model<PayLog> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Date recharge;

    private String cardNo;

    private Integer cardDate;

    private Date foundDate;

    /**
     * 操作人员id，哪个用户为学生充值的
     */
    @TableField("operator_id")
    private Integer operatorId;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}