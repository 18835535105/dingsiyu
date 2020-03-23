package com.zhidejiaoyu.common.pojo;


import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("rechargeable_card")
public class RechargeableCard extends Model<RechargeableCard> {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 充值卡名
     */
    @NotBlank(message = "充值卡名称不能为空！")
    @TableField("name")
    private String name;

    /**
     * 期限
     */
    @Range(min = 1, max = 366, message = "充值卡天数在 1~366 天之间！")
    @NotNull(message = "充值卡天数不能为空！")
    @TableField("time")
    private Integer time;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;

    /**
     * 状态
     */
    @TableField("state")
    private Integer state;

    @TableField("update_time")
    private Date updateTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
