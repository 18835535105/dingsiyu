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
public class OpenUnitLog extends Model<OpenUnitLog> {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long currentUnitId;

    private Long nextUnitId;

    private Date createTime;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}