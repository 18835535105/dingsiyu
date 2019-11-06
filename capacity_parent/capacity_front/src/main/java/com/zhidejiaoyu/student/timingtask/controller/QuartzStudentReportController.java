package com.zhidejiaoyu.student.timingtask.controller;

import com.zhidejiaoyu.student.timingtask.service.QuartzStudentReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 学生报表相关定时任务
 *
 * @author: wuchenxi
 * @Date: 2019/11/5 10:51
 */
@RestController
@RequestMapping("/quartz/student/report")
public class QuartzStudentReportController {

    @Resource
    private QuartzStudentReportService quartzStudentReportService;

    /**
     * 导出校区学生登录及在线时长信息
     *
     * @return
     */
    @GetMapping("/exportStudentWithSchool")
    public void exportStudentWithSchool() {
        quartzStudentReportService.exportStudentWithSchool();
    }

    @RequestMapping("exportStudentPay")
    public void exportStudentPay() {
        quartzStudentReportService.exportStudentPay();
    }

}
