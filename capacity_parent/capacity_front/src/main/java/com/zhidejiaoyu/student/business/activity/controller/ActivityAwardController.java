package com.zhidejiaoyu.student.business.activity.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.activity.service.ActivityAwardService;
import com.zhidejiaoyu.student.business.controller.BaseController;
import org.springframework.web.bind.annotation.*;

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
     * 活动排名
     *
     * @param type 1:校区活动排行；2：同服务器排行
     * @return
     */
    @GetMapping("/rank")
    public ServerResponse<Object> rank(@RequestParam(required = false, defaultValue = "1") Integer type) {
        return activityAwardService.rank(type);
    }

    /**
     * 领取奖励
     *
     * @param awardGold 奖励金币数
     * @return
     */
    @PostMapping("/getAward")
    public ServerResponse<Object> getAward(Integer awardGold) {
        if (awardGold == null) {
            throw new ServiceException(400, "awardGold can't be null!");
        }
        return activityAwardService.getAward(awardGold);
    }

    /**
     * 统计可领取奖励个数
     *
     * @return
     */
    @GetMapping("/getAwardCount")
    public ServerResponse<Object> getAwardCount() {
        return activityAwardService.getAwardCount();
    }
}
