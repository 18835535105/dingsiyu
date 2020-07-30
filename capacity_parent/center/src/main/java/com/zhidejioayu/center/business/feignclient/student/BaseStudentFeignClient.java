package com.zhidejioayu.center.business.feignclient.student;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.student.manage.EditStudentVo;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.ExportRechargePayResultVO;
import com.zhidejiaoyu.common.vo.student.studentinfowithschool.StudentInfoSchoolResult;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 保存学生金币
     *
     * @param openId
     * @param gold
     */
    @PostMapping("/student/saveGold")
    void saveGold(@RequestParam String openId, @RequestParam Integer gold);

    /**
     * 获取学生年级
     *
     * @param openId
     * @return
     */
    @GetMapping("/student/getStudentGradeByOpenId")
    String getStudentGradeByOpenId(@RequestParam String openId);

    /**
     * 教师后台获取需要编辑的学生信息
     *
     * @param uuid  学生uuid
     * @return
     */
    @GetMapping("/student/edit/getEditStudentVoByUuid")
    ServerResponse<EditStudentVo> getEditStudentVoByUuid(@RequestParam String uuid);
}

