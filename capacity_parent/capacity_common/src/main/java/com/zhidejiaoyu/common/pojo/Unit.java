package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

/***
 * 单元
 * 
 * @author Administrator
 */
public class Unit implements Serializable {
	/** id */
    private Long id;

    /** 课程主建 */
    private Long courseId;

    /** 单元名 */
    private String unitName;

    /** 课程单元拼接名 */
    private String jointName;
    
    /** 删除状态 1:未删除（默认），2：删除 */
    private int delStatus;
    
    /** 单元顺序，用于判断当前单元的下一单元是哪个 */
    private Integer unitIndex;
    
    
    
    public Integer getUnitIndex() {
		return unitIndex;
	}

	public void setUnitIndex(Integer unitIndex) {
		this.unitIndex = unitIndex;
	}

	public int getDelStatus() {
		return delStatus;
	}

	public void setDelStatus(int delStatus) {
		this.delStatus = delStatus;
	}

	@Override
	public String toString() {
		return "Unit [id=" + id + ", courseId=" + courseId + ", unitName=" + unitName + ", jointName=" + jointName
				+ "]";
	}

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
        this.unitName = unitName == null ? null : unitName.trim();
    }

    public String getJointName() {
        return jointName;
    }

    public void setJointName(String jointName) {
        this.jointName = jointName == null ? null : jointName.trim();
    }
}