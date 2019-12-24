package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudentReportService;
import org.junit.Test;

import javax.annotation.Resource;

public class QuartzStudentReportServiceImplTest extends BaseTest {

    @Resource
    private QuartzStudentReportService quartzStudentReportService;

    @Test
    public void statisticsStudentWithSchoolInfo() {
        quartzStudentReportService.exportStudentWithSchool();
    }

    @Test
    public void exportStudentPay() {
        quartzStudentReportService.exportStudentPay();
    }
}