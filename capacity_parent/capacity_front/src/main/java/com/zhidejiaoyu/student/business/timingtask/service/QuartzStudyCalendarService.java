package com.zhidejiaoyu.student.business.timingtask.service;

/**
 * 学习日历定时任务
 *
 * @author: wuchenxi
 * @date: 2020/3/30 15:59:59
 */
public interface QuartzStudyCalendarService extends BaseQuartzService {

    /**
     * 每日新增学习摘要数据
     */
    void initStudentDailyLearning();

    /**
     * 每日新增学习详情页数据
     */
    void initLearningDetails();

    /**
     * 每日新增金币变化记录数据
     */
    void initGoldRecord();

    /**
     * 每日新增打卡机点赞记录数据
     */
    void initPunchRecord();
}
