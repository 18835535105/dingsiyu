package com.zhidejiaoyu.student.business.activity.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.activity.service.ActivityAwardService;
import com.zhidejiaoyu.student.business.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 每周活动奖励接口
 *
 * @author: wuchenxi
 * @date: 2020/5/27 14:18:18
 */
@RestController
@RequestMapping("/activity/award")
public class ActivityAwardController extends BaseController {

    @Resource
    private ActivityAwardService activityAwardService;

    /**
     * 奖励列表数据
     *
     * @return
     */
    @GetMapping("/awardList")
    public ServerResponse<Object> awardList() {
        return activityAwardService.awardList();
    }

    /**
     * 校区活动排名
     *
     * @return
     */
    @GetMapping("/rank")
    public ServerResponse<Object> rank() {
        return activityAwardService.rank();
    }
}
