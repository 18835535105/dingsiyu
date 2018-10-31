package com.zhidejiaoyu.common.pojo;

import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

public class StudentUnit extends Model<StudentUnit> {
    @TableId(type = IdType.AUTO)
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

    @Override
    protected Serializable pkVal() {
        return this.id;
    }
}