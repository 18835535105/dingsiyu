package com.zhidejiaoyu.common.vo.student.studentinfowithschool;

import lombok.Data;

import java.io.Serializable;

/**
 * 校区下学生当天在线信息明细
 *
 * @author: wuchenxi
 * @Date: 2019/11/5 14:21
 */
@Data
public class StudentInfoSchoolDetail implements Serializable {

    /**
     * 学校名
     */
    private String schoolName;

    /**
     * 学生账号
     */
    private String account;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 在线时长
     */
    private Long onlineTime;

    /**
     * 统计日期
     */
    private String time;

    /**
     * 学生创建日期
     */
    private String createTime;

    /**
     * 是否充课, 0：未充课；非0：已充课
     */
    private String paid;
}
