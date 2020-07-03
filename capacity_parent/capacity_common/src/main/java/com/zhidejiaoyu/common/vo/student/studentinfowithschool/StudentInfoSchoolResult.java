package com.zhidejiaoyu.common.vo.student.studentinfowithschool;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 校区当天在线和登录人数信息汇总
 *
 * @author: wuchenxi
 * @date: 2020/7/3 17:48:48
 */
@Data
public class StudentInfoSchoolResult implements Serializable {

    private List<StudentInfoSchoolDetail> studentInfoSchoolDetailList;
    private List<StudentInfoSchoolSummary> studentInfoSchoolSummaries;
}
