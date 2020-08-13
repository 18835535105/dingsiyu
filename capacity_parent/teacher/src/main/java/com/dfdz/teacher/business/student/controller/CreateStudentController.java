package com.dfdz.teacher.business.student.controller;

import com.dfdz.teacher.business.student.service.CreateStudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 学生账号生成相关逻辑
 *
 * @author: liumaoyu
 * @Date: 2019/11/26 09:53
 */
@RestController
@RequestMapping("/student/createStudentCount")
public class CreateStudentController {

    @Resource
    private CreateStudentService createStudentService;

    /**
     * 获取当前校区还可生成招生账号的数量
     *
     * @return
     */
    @GetMapping("/canCreateCount")
    public Object canCreateCount(String openId) {
        return createStudentService.canCreateCount(openId);
    }
}
