package com.zhidejiaoyu.student.timingtask.service;

/**
 * 学生报表相关定时任务
 * <br>
 * <a href="https://www.showdoc.cc/65694455382333?page_id=2858504713316437">定时任务说明文档</a>
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
    void exportStudentWithSchool();

    /**
     * 每日一点15统计昨天各校区新增课时人源
     *
     * @return
     */
    void exportStudentPay();
}
