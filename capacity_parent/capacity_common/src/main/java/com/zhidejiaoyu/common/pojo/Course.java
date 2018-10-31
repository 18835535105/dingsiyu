package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 课程
 * 
 * @author Administrator
 */

/**
 * @author Administrator
 *
 */
public class Course extends Model<Course> {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 学段 */
    private String studyParagraph;

    /** 年级 */
    private String grade;

    /** 版本 */
    private String version;

    /** 标签 */
    private String label;

    /** 课本封面图名     
     1.年级 取首字母
     2.版本 取首字母
     3.标签 取首字母
         123拼接成的图片名*/
    private String pictureName;
    
    /** 封面图url全地址 */
    private String pictureUrl;

    /** 课程名 */
    private String courseName;

    /** 删除状态 */
    private int delStatus;
    
	/** ! 单元数 */
    private int unitCount;
    /** ! 开启状态中文 */
    private String stat;
    

    public int getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}

    
    public String getStat() {
		return stat;
	}

	public void setStat(String stat) {
		this.stat = stat;
	}

	public int getUnitCount() {
		return unitCount;
	}

	public void setUnitCount(int unitCount) {
		this.unitCount = unitCount;
	}

	/** 状态 */
    private Integer status;

    /** 创建时间 */
    private String createTime;

    /** 更新时间 */
    private Date updateTime;

	@Override
	public String toString() {
		return "Course [id=" + id + ", studyParagraph=" + studyParagraph + ", grade=" + grade + ", version=" + version
				+ ", label=" + label + ", pictureName=" + pictureName + ", pictureUrl=" + pictureUrl + ", courseName="
				+ courseName + ", unitCount=" + unitCount + ", stat=" + stat + ", status=" + status + ", createTime="
				+ createTime + ", updateTime=" + updateTime + "]";
	}

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
        this.studyParagraph = studyParagraph == null ? null : studyParagraph.trim();
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade == null ? null : grade.trim();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version == null ? null : version.trim();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label == null ? null : label.trim();
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName(String pictureName) {
        this.pictureName = pictureName == null ? null : pictureName.trim();
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl == null ? null : pictureUrl.trim();
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}