package com.zhidejiaoyu.student.business.activity.controller;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.activity.service.ActivityAwardService;
import com.zhidejiaoyu.student.business.controller.BaseController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
}
