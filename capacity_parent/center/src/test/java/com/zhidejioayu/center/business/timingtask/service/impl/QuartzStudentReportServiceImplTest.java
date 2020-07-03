package com.zhidejioayu.center.business.timingtask.service.impl;

import com.zhidejioayu.center.business.timingtask.service.QuartzStudentReportService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: wuchenxi
 * @date: 2020/7/3 19:10:10
 */
@RunWith(SpringRunner.class)
@SpringBootTest
class QuartzStudentReportServiceImplTest {

    @Resource
    private QuartzStudentReportService quartzStudentReportService;

    @Test
    void exportStudentWithSchool() {
        quartzStudentReportService.exportStudentWithSchool();
    }

    @Test
    void exportStudentPay() {
        quartzStudentReportService.exportStudentPay();
    }
}
