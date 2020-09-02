package com.zhidejiaoyu.student.business.timingtask.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzGradeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author wuchenxi
 * @date 2020-09-02 20:19:39
 */
@RestController
@RequestMapping("/quartz/grade")
public class QuartzGradeController {

    @Resource
    private QuartzGradeService quartzGradeService;

    /**
     * 升级学生年级
     *
     * @return
     */
    @PostMapping("/updateGrade")
    public ServerResponse<Object> updateGrade() {
        quartzGradeService.updateGrade();
        return ServerResponse.createBySuccess();
    }
}
