package com.zhidejiaoyu.common.vo.student.studentinfowithschool;

import lombok.Data;

/**
 * 校区下学生当天在线信息明细
 *
 * @author: wuchenxi
 * @Date: 2019/11/5 14:21
 */
@Data
public class StudentInfoSchoolDetail {

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
}
