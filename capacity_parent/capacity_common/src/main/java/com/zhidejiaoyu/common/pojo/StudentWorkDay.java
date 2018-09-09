package com.zhidejiaoyu.common.pojo;

public class StudentWorkDay {
    private Long id;

    private Long studentId;

    private String workDayBegin;

    private String workDayEnd;

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

    public String getWorkDayBegin() {
        return workDayBegin;
    }

    public void setWorkDayBegin(String workDayBegin) {
        this.workDayBegin = workDayBegin == null ? null : workDayBegin.trim();
    }

    public String getWorkDayEnd() {
        return workDayEnd;
    }

    public void setWorkDayEnd(String workDayEnd) {
        this.workDayEnd = workDayEnd == null ? null : workDayEnd.trim();
    }
}