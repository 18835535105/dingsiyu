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
public class Worship extends Model<Worship> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentIdWorship;

    private Long studentIdByWorship;

    private Date worshipTime;

    private Integer state;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}
