package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 时长表,用于记录学生在线时长和学习有效时长
 * 登录时间：
 * 学生登录时将当前登录时间记录到session中
 * 有效时长：
 * 学生每次进入学习页面开始记录有效时长直至学生从学习页面离开（前端先存储到本地，为本次登录之后所有有效时长相加，待学生退出登录后，传给服务器端）
 * 在线时长：
 * 记录学生从登录到系统直至退出系统之间的时间
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
public class Duration implements Serializable {
    private Long id;

    private Long studentId;

    private Long courseId;

    private Long unitId;

    /**
     * 学生退出时由前端发送给服务器记录,学生本次登录期间学习中所有有效时长总和,单位：秒(s)
     */
    private Long validTime;

    /**
     * 学生从登录到退出的在线时长
     */
    private Long onlineTime;

    /**
     * 学生登录系统时间
     */
    private Date loginTime;

    /**
     * 学生退出登录时间
     */
    private Date loginOutTime;

    /**
     * 学习模块，区分各个学习模块的时长，1：慧记忆；2：慧听写；3：慧默写；4：例句听力；5：例句翻译；6：例句默写
     */
    private Integer studyModel;

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

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getValidTime() {
        return validTime;
    }

    public void setValidTime(Long validTime) {
        this.validTime = validTime;
    }

    public Long getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(Long onlineTime) {
        this.onlineTime = onlineTime;
    }

    public Date getLoginTime() {
        return loginTime;
    }

    public void setLoginTime(Date loginTime) {
        this.loginTime = loginTime;
    }

    public Date getLoginOutTime() {
        return loginOutTime;
    }

    public void setLoginOutTime(Date loginOutTime) {
        this.loginOutTime = loginOutTime;
    }

    public Integer getStudyModel() {
        return studyModel;
    }

    public void setStudyModel(Integer studyModel) {
        this.studyModel = studyModel;
    }

    @Override
    public String toString() {
        return "Duration{" +
                "id=" + id +
                ", studentId=" + studentId +
                ", courseId=" + courseId +
                ", unitId=" + unitId +
                ", validTime=" + validTime +
                ", onlineTime=" + onlineTime +
                ", loginTime=" + loginTime +
                ", loginOutTime=" + loginOutTime +
                ", studyModel=" + studyModel +
                '}';
    }
}