package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.activerecord.Model;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 学生学习计划表
 * </p>
 *
 * @author zdjy
 * @since 2019-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("student_study_plan")
public class StudentStudyPlan extends Model<StudentStudyPlan> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("student_id")
    private Long studentId;

    @TableField("course_id")
    private Long courseId;
    /**
     * 学习计划起始单元
     */
    @TableField("start_unit_id")
    private Long startUnitId;
    /**
     * 学习计划结束单元
     */
    @TableField("end_unit_id")
    private Long endUnitId;

    /**
     * 1:单词计划课程；2：句型计划课程；3：课文计划课程
     */
    private Integer type;

    @TableField("update_time")
    private Date updateTime;

    /**
     * 1：当前计划还没有一学习；2：学习完毕
     */
    private Integer complete;

    /**
     * 当前计划当前学习的遍数
     */
    @TableField("current_study_count")
    private Integer currentStudyCount;

    /**
     * 当前计划需要学习的总遍数
     */
    @TableField("total_study_count")
    private Integer totalStudyCount;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
