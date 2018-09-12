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