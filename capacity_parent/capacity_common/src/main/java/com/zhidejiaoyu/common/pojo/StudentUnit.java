package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 学生可以学习的课程和单元
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class StudentUnit extends Model<StudentUnit> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    /**
     * 单词部分单元解锁状态
     */
    private Integer wordStatus;

    /**
     * 例句部分单元解锁状态
     */
    private Integer sentenceStatus;

    private int type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}