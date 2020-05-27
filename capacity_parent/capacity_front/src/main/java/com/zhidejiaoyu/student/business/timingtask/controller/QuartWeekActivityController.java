package com.zhidejiaoyu.student.business.timingtask.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.timingtask.service.QuartWeekActivityService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 每周活动定时任务
 *
 * @author: wuchenxi
 * @date: 2020/5/27 10:26:26
 */
@RestController
@RequestMapping("/quart/weekActivity")
public class QuartWeekActivityController {

    @Resource
    private QuartWeekActivityService quartWeekActivityService;

    /**
     * 每周一0点初始化校区活动排行
     *
     * @return
     */
    @PostMapping("/init")
    public Object init() {
        quartWeekActivityService.init();
        return ServerResponse.createBySuccess();
    }
}
