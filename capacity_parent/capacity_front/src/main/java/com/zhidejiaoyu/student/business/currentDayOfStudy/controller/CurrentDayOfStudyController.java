package com.zhidejiaoyu.student.business.currentDayOfStudy.controller;

import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.controller.BaseController;
import com.zhidejiaoyu.student.business.currentDayOfStudy.service.CurrentDayOfStudyService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/currentDayOfStudy")
public class CurrentDayOfStudyController extends BaseController {

    @Resource
    private CurrentDayOfStudyService currentDayOfStudyService;

    @RequestMapping("/getCurrentDayOfStudy")
    public ServerResponse<Object> getCurrentDayOfStudy() {
        return currentDayOfStudyService.getCurrentDayOfStudy();
    }

    /**
     * 保存记录
     *
     * @param currentDayOfStudy
     * @return
     */
    @PostMapping("/save")
    public Boolean save(@RequestBody CurrentDayOfStudy currentDayOfStudy) {
        return currentDayOfStudyService.save(currentDayOfStudy);
    }
}

