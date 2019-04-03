package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * <p>
 * 单元表
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
@TableName("sentence_unit")
public class SentenceUnit extends Model<SentenceUnit> {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    @TableField("course_id")
    private Long courseId;
    /**
     * 单元名
     */
    @TableField("unit_name")
    private String unitName;
    /**
     * 课程单元拼接名 - 用与导入词/句关联单元使用
     */
    @TableField("joint_name")
    private String jointName;
    /**
     * 删除状态: 1:未删除(默认)  2:已删除
     */
    private Integer delStatus;
    /**
     * 单元顺序，用于判断当前单元的下一单元是哪个
     */
    @TableField("unit_index")
    private Integer unitIndex;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public String getJointName() {
        return jointName;
    }

    public void setJointName(String jointName) {
        this.jointName = jointName;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    public Integer getUnitIndex() {
        return unitIndex;
    }

    public void setUnitIndex(Integer unitIndex) {
        this.unitIndex = unitIndex;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SentenceUnit{" +
        "id=" + id +
        ", courseId=" + courseId +
        ", unitName=" + unitName +
        ", jointName=" + jointName +
        ", delStatus=" + delStatus +
        ", unitIndex=" + unitIndex +
        "}";
    }
}
