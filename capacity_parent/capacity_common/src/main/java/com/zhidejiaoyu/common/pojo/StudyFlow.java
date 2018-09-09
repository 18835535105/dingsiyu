package com.zhidejiaoyu.common.pojo;

public class StudyFlow {
    private Long id;

    private Integer nextTrueFlow;

    private Integer nextFalseFlow;

    private String modelName;

    private String flowName;

    private Integer type;

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
}