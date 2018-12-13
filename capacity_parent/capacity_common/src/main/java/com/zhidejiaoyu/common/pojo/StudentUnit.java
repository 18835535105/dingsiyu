package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.web.bind.annotation.RequestMapping;

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

    /**
     * 学生学习课程版本 1,智能版  2,清学版
     */
    private Integer type;

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}