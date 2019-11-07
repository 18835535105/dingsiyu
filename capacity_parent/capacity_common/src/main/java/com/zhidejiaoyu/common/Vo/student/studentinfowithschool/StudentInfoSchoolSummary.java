package com.zhidejiaoyu.common.Vo.student.studentinfowithschool;

import lombok.Data;

/**
 * 校区下学生当天登录人数汇总
 *
 * @author: wuchenxi
 * @Date: 2019/11/5 17:05
 */
@Data
public class StudentInfoSchoolSummary {
    private String schoolName;

    private Long studentCount;

    /**
     * 汇总日期
     */
    private String time;
}
