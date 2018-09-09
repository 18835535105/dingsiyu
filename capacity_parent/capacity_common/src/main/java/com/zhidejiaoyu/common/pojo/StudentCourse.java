package com.zhidejiaoyu.common.pojo;

import java.util.Date;

/**
 * 学生-课程关系表
 * 我的课程中需要展示的课程信息
 * @date 2018-06-28
 */
public class StudentCourse {
    private Long id;

    private Long studentId;

    private Long courseId;

    private String courseName;

    /**
     * 1：单词课程；2：例句课程
     */
    private Integer type;

    /**
     * 课程生成时间
     */
    private String updateTime;

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

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

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName == null ? null : courseName.trim();
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}