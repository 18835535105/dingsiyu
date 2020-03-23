package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
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
