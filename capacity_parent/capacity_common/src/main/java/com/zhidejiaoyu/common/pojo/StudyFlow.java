package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

public class StudyFlow extends Model<StudyFlow> {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Integer nextTrueFlow;

    private Integer nextFalseFlow;

    private String modelName;

    private String flowName;

    private Integer type;
    
    private Long courseId;
    
    private Long unitId;
    
    private String courseName;
    
    private String unitName;
    
    /** true代表是新生, 需要走独立的奖励规则*/
    private boolean neogenesis;
    
    public boolean getNeogenesis() {
		return neogenesis;
	}

	public void setNeogenesis(boolean neogenesis) {
		this.neogenesis = neogenesis;
	}

	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNextTrueFlow() {
        return nextTrueFlow;
    }

    public void setNextTrueFlow(Integer nextTrueFlow) {
        this.nextTrueFlow = nextTrueFlow;
    }

    public Integer getNextFalseFlow() {
        return nextFalseFlow;
    }

    public void setNextFalseFlow(Integer nextFalseFlow) {
        this.nextFalseFlow = nextFalseFlow;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName == null ? null : modelName.trim();
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName == null ? null : flowName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}