package com.zhidejioayu.center.business.feignclient.wxrobot;

import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.DailyStateVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 12:30:30
 */
public interface BaseWxRobotFeignClient {

    /**
     * 获取各个学生的当天的飞行记录及打卡状态
     *
     * @param account
     * @return
     */
    @GetMapping("/robot/getDailyState")
    List<DailyStateVO> getDailyState(@RequestParam String account);
}

