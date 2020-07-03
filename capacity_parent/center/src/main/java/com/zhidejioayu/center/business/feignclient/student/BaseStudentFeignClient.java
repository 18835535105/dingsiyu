package com.zhidejioayu.center.business.feignclient.student;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.ExportRechargePayResultVO;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolResult;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
public interface BaseStudentFeignClient {

    /**
     * 获取校区学生登录及在线时长信息
     *
     * @return
     */
    @GetMapping("/student/report/getStudentLoginAndTimeInfo")
    ServerResponse<StudentInfoSchoolResult> getStudentLoginAndTimeInfo();

    /**
     * 获取学生充值信息
     *
     * @return
     */
    @GetMapping("/student/report/getStudentPayInfo")
    ServerResponse<ExportRechargePayResultVO> getStudentPayInfo();
}

