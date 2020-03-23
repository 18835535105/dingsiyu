package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
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