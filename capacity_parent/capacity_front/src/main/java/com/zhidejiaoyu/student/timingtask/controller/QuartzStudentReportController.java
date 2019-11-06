package com.zhidejiaoyu.student.timingtask.controller;

import com.zhidejiaoyu.student.timingtask.service.QuartzStudentReportService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
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

    @RequestMapping("exportStudentPay")
    public Object exportStudentPay(HttpServletResponse response){
        return quartzStudentReportService.exportStudentPay(response);
    }

}
