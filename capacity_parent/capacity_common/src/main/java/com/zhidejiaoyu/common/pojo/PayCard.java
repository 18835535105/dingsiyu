package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = false)
public class PayCard extends Model<PayCard> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private String cardNo;

    private Integer validityTime;

    private Date createTime;

    private Date useTime;

    private String useUser;

    private String createUser;

    private String goodsUser;

    private Integer cardNum;

    private Integer canUse;

    /**
     * 收货人id
     */
    @TableField("goods_user_id")
    private Integer goodsUserId;

    @TableField("type")
    private Integer type;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}