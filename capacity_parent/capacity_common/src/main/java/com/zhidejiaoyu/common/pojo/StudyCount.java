package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class StudyCount implements Serializable {
    private Long id;

    private Long studentId;

    private Long courseId;

    private Integer count;

    private Integer studyCount;

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

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getStudyCount() {
        return studyCount;
    }

    public void setStudyCount(Integer studyCount) {
        this.studyCount = studyCount;
    }
}