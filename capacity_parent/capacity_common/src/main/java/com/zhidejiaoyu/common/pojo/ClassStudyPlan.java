package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 班级学习计划表
 * </p>
 *
 * @author zdjy
 * @since 2019-01-15
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("class_study_plan")
public class ClassStudyPlan extends Model<ClassStudyPlan> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField("class_id")
    private Long classId;

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

    @TableField("update_time")
    private Date updateTime;

    private Integer type;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
