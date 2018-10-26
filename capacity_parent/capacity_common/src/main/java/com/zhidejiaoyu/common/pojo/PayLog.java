package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}