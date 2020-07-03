package com.zhidejiaoyu.student.business.student.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.ExportRechargePayResultVO;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolResult;
import com.zhidejiaoyu.student.business.student.service.StudentReportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 查询学生学生需要导出的报表
 *
 * @author: wuchenxi
 * @Date: 2019/11/5 10:51
 */
@RestController
@RequestMapping("/student/report")
public class StudentReportController {

    @Resource
    private StudentReportService quartzStudentReportService;

    /**
     * 导出校区学生登录及在线时长信息
     *
     * @return
     */
    @GetMapping("/getStudentLoginAndTimeInfo")
    public ServerResponse<StudentInfoSchoolResult> getStudentLoginAndTimeInfo() {
       return quartzStudentReportService.getStudentLoginAndTimeInfo();
    }

    /**
     * 获取学生充值信息
     */
    @RequestMapping("/getStudentPayInfo")
    public ServerResponse<ExportRechargePayResultVO> getStudentPayInfo() {
       return quartzStudentReportService.getStudentPayInfo();
    }

}
