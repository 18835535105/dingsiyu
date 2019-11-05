package com.zhidejiaoyu.student.timingtask.service;

import com.zhidejiaoyu.common.utils.server.ServerResponse;

/**
 * 学生报表相关定时任务
 *
 * @author: wuchenxi
 * @Date: 2019/11/5 10:53
 */
public interface QuartzStudentReportService {

    /**
     * 每日01:00:00统计昨天各个校区在线学生在线时长信息
     *
     * @return
     */
    ServerResponse statisticsStudentWithSchoolInfo();
}
