package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}