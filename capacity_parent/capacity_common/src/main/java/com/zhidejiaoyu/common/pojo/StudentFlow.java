package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class StudentFlow extends Model<StudentFlow> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long currentFlowId;

    private Long currentFlowMaxId;

    private Integer timeMachine;

    private Integer presentFlow;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}