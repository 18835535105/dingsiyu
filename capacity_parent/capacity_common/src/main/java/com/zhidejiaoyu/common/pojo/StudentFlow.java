package com.zhidejiaoyu.common.pojo;

public class StudentFlow {
    private Long id;

    private Long studentId;

    private Long currentFlowId;

    private Long currentFlowMaxId;

    private Integer timeMachine;

    private Integer presentFlow;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCurrentFlowId() {
        return currentFlowId;
    }

    public void setCurrentFlowId(Long currentFlowId) {
        this.currentFlowId = currentFlowId;
    }

    public Long getCurrentFlowMaxId() {
        return currentFlowMaxId;
    }

    public void setCurrentFlowMaxId(Long currentFlowMaxId) {
        this.currentFlowMaxId = currentFlowMaxId;
    }

    public Integer getTimeMachine() {
        return timeMachine;
    }

    public void setTimeMachine(Integer timeMachine) {
        this.timeMachine = timeMachine;
    }

    public Integer getPresentFlow() {
        return presentFlow;
    }

    public void setPresentFlow(Integer presentFlow) {
        this.presentFlow = presentFlow;
    }
}