package com.zhidejiaoyu.student.business.timingtask.controller;

import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzAwardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 奖励相关定时任务
 * <br>
 * <a href="https://www.showdoc.cc/65694455382333?page_id=2858504713316437">定时任务说明文档</a>
 *
 * @author: wuchenxi
 * @Date: 2019/11/25 16:54
 */
@RestController
@RequestMapping("/quartz/award")
public class QuartzAwardController {

    @Resource
    private QuartzAwardService quartzAwardService;

    /**
     * 删除学生日奖励信息
     */
    @PostMapping("/deleteDailyAward")
    public void deleteDailyAward() {
        this.quartzAwardService.deleteDailyAward();
    }

    /**
     * 初始化所有学生排行缓存信息（非定时任务，手动执行）
     *
     * @return
     */
    @PostMapping("/initRankCaches")
    public ServerResponse initRankCaches() {
        quartzAwardService.initRankCaches();
        return ServerResponse.createBySuccess();
    }

    /**
     * 初始化所有指定学生排行缓存信息（非定时任务，手动执行）
     *
     * @return
     */
    @GetMapping("/initRankCache")
    public ServerResponse initRankCache(Long studentId) {
        quartzAwardService.initRankCache(studentId);
        return ServerResponse.createBySuccess();
    }

    /**
     * 初始化怪物勋章（非定时任务，手动执行）
     *
     * @return
     */
    @PostMapping("/initMonsterMedal")
    public ServerResponse initMonsterMedal() {
        quartzAwardService.initMonsterMedal();
        return ServerResponse.createBySuccess();
    }

    /**
     * 清除删除状态学生的排行缓存
     *
     * @return
     */
    @PostMapping("/deleteRank")
    public ServerResponse deleteRank() {
        quartzAwardService.deleteRank();
        return ServerResponse.createBySuccess();
    }

}
