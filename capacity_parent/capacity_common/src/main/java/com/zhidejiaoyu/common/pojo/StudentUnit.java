package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class StudentUnit implements Serializable {
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    /**
     * 单词部分单元解锁状态
     */
    private Integer wordStatus;

    /**
     * 例句部分单元解锁状态
     */
    private Integer sentenceStatus;

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

    public Long getUnitId() {
        return unitId;
    }

    public void setUnitId(Long unitId) {
        this.unitId = unitId;
    }

    public Integer getWordStatus() {
        return wordStatus;
    }

    public void setWordStatus(Integer wordStatus) {
        this.wordStatus = wordStatus;
    }

    public Integer getSentenceStatus() {
        return sentenceStatus;
    }

    public void setSentenceStatus(Integer sentenceStatus) {
        this.sentenceStatus = sentenceStatus;
    }
}