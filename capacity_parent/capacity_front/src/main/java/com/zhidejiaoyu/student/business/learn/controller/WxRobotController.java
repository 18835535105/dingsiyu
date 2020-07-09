package com.zhidejiaoyu.student.business.learn.controller;

import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.DailyStateVO;
import com.zhidejiaoyu.student.business.learn.service.WxRobotService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * 微信对话机器人定时任务调用接口
 *
 * @author: wuchenxi
 * @date: 2020/4/7 17:55:55
 */
@RestController
@RequestMapping("/robot")
public class WxRobotController {

    @Resource
    private WxRobotService wxRobotService;

    /**
     * 获取所有学生当天的飞行记录及打卡状态
     *
     * @param account 学生账号，多个账号间用英文,隔开
     * @return
     */
    @GetMapping("/getDailyState")
    public List<DailyStateVO> getDailyState(@RequestParam String account) {
        return wxRobotService.getDailyState(account);
    }
}
