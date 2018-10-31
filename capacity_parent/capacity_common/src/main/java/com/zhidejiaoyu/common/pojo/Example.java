package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

public class Example extends Model<Example> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String exampleEnglish;

    private String exampleChinese;

    private String explain;

    /**
     * 状态 1：开启，2：关闭
     */
    private Integer status;

    private Date createTime;

    private Date updateTiem;

    private String courseUnit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExampleEnglish() {
        return exampleEnglish;
    }

    public void setExampleEnglish(String exampleEnglish) {
        this.exampleEnglish = exampleEnglish == null ? null : exampleEnglish.trim();
    }

    public String getExampleChinese() {
        return exampleChinese;
    }

    public void setExampleChinese(String exampleChinese) {
        this.exampleChinese = exampleChinese == null ? null : exampleChinese.trim();
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain == null ? null : explain.trim();
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

    public Date getUpdateTiem() {
        return updateTiem;
    }

    public void setUpdateTiem(Date updateTiem) {
        this.updateTiem = updateTiem;
    }

    public String getCourseUnit() {
        return courseUnit;
    }

    public void setCourseUnit(String courseUnit) {
        this.courseUnit = courseUnit == null ? null : courseUnit.trim();
    }

	@Override
	public String toString() {
		return "Example [id=" + id + ", exampleEnglish=" + exampleEnglish + ", exampleChinese=" + exampleChinese
				+ ", explain=" + explain + ", status=" + status + ", createTime=" + createTime + ", updateTiem="
				+ updateTiem + ", courseUnit=" + courseUnit + "]";
	}


    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}