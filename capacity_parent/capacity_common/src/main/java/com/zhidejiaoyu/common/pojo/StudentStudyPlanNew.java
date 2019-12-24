package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 学生学习计划表
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class StudentStudyPlanNew extends Model<StudentStudyPlanNew> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private Long studentId;
    private Long courseId;
    /**
     * 单元id
     */
    private Long unitId;
    /**
     * 分组序号
     */
    private Integer group;
    /**
     * 1：当前计划还没有学习或还没有学习完毕；2：学习完毕
     */
    private Integer complete;
    /**
     * 当前计划当前学习的遍数
     */
    private Integer currentStudyCount;
    /**
     * 当前计划需要学习的总遍数
     */
    private Integer totalStudyCount;
    /**
     * 学习流程表id（存放难、易模块的流程id）
     */
    private Integer flowId;
    /**
     * 难易类型：1：简单类型；2：难类型
     */
    private Integer easyOrHard;
    /**
     * 时间优先级，默认“1”
     */
    private Integer timeLevel;
    /**
     * 基础优先级，默认“1”
     */
    private Integer baseLevel;
    /**
     * 错误优先级，默认“1”
     */
    private Integer errorLevel;
    /**
     * 最终优先级，默认“1”
     */
    private Integer finalLevel;
    private Date updateTime;


    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
