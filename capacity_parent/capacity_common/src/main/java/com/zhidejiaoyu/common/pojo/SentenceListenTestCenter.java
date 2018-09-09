package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class SentenceListenTestCenter implements Serializable {
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long vocabularyId;

    private Long exampleId;

    private Integer status;

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

    public Long getVocabularyId() {
        return vocabularyId;
    }

    public void setVocabularyId(Long vocabularyId) {
        this.vocabularyId = vocabularyId;
    }

    public Long getExampleId() {
        return exampleId;
    }

    public void setExampleId(Long exampleId) {
        this.exampleId = exampleId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}