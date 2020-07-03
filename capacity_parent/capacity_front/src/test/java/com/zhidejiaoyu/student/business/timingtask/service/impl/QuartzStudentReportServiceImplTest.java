package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.student.business.student.service.StudentReportService;
import org.junit.Test;

import javax.annotation.Resource;

public class QuartzStudentReportServiceImplTest extends BaseTest {

    @Resource
    private StudentReportService quartzStudentReportService;

    @Test
    public void statisticsStudentWithSchoolInfo() {
        quartzStudentReportService.getStudentLoginAndTimeInfo();
    }

    @Test
    public void exportStudentPay() {
        quartzStudentReportService.getStudentPayInfo();
    }
}
