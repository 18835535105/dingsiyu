package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudyCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author: wuchenxi
 * @date: 2020/3/30 16:03:03
 */
@Slf4j
@Service
public class QuartzStudyCalendarServiceImpl implements QuartzStudyCalendarService {

    @Value("${quartz.port}")
    private int port;

    /**
     * 每天 0：02 分执行
     */
    @Scheduled(cron = "0 2 0 * * ?")
    public void initStudyCalendar() {
        if (checkPort(port)) {
            return;
        }
        log.info("初始化学习日历开始....");
        this.initStudentDailyLearning();
        this.initLearningDetails();
        this.initGoldRecord();
        this.initPunchRecord();
        log.info("初始化学习日历结束！");
    }

    @Override
    public void initStudentDailyLearning() {

    }

    @Override
    public void initLearningDetails() {

    }

    @Override
    public void initGoldRecord() {

    }

    @Override
    public void initPunchRecord() {

    }
}
