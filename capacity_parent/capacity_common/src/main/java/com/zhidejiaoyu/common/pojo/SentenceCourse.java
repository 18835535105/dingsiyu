package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 课程表
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
@TableName("sentence_course")
public class SentenceCourse extends Model<SentenceCourse> {

    private static final long serialVersionUID = 1L;

    /**
     * 课程表主建
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 学段
     */
    @TableField("study_paragraph")
    private String studyParagraph;
    /**
     * 年级
     */
    private String grade;
    /**
     * 版本
     */
    private String version;
    /**
     * 标签
     */
    private String label;
    /**
     * 课本封面图名
     */
    @TableField("picture_name")
    private String pictureName;
    /**
     * 课本封面图url地址
     */
    @TableField("picture_url")
    private String pictureUrl;
    /**
     * 课程名
     */
    @TableField("course_name")
    private String courseName;
    /**
     * 状态, 1=开启(默认), 2=关闭
     */
    private Integer status;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除状态: 1:未删除(默认)  2:已删除
     */
    @TableField("delStatus")
    private Integer delStatus;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStudyParagraph() {
        return studyParagraph;
    }

    public void setStudyParagraph(String studyParagraph) {
        this.studyParagraph = studyParagraph;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getDelStatus() {
        return delStatus;
    }

    public void setDelStatus(Integer delStatus) {
        this.delStatus = delStatus;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "SentenceCourse{" +
        "id=" + id +
        ", studyParagraph=" + studyParagraph +
        ", grade=" + grade +
        ", version=" + version +
        ", label=" + label +
        ", pictureName=" + pictureName +
        ", pictureUrl=" + pictureUrl +
        ", courseName=" + courseName +
        ", status=" + status +
        ", createTime=" + createTime +
        ", updateTime=" + updateTime +
        ", delStatus=" + delStatus +
        "}";
    }
}
