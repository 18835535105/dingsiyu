package com.zhidejiaoyu.student.business.timingtask.controller;

import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudentReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 学生报表相关定时任务
 * <br>
 * <a href="https://www.showdoc.cc/65694455382333?page_id=2858504713316437">定时任务说明文档</a>
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

    @RequestMapping("/exportStudentPay")
    public void exportStudentPay() {
        quartzStudentReportService.exportStudentPay();
    }

}
