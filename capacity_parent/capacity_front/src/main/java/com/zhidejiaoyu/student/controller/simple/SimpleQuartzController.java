package com.zhidejiaoyu.student.controller.simple;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.simple.SimpleQuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quartz")
public class SimpleQuartzController {

    @Autowired
    private SimpleQuartzService simpleQuartzService;

    /**
     * 更新题型消息中学生账号到期提醒
     *
     * @return
     */
    @PostMapping("/updateNews")
    public ServerResponse updateNews() {
        simpleQuartzService.updateNews();
        return ServerResponse.createBySuccess();
    }

    /**
     * 学生能量清零
     *
     * @return
     */
    @PostMapping("/updateEnergy")
    public ServerResponse updateEnergy() {
        this.simpleQuartzService.updateEnergy();
        return ServerResponse.createBySuccess();
    }

    /**
     * 冻结的学生每日增加有效期
     *
     * @return
     */
    @PostMapping("/updateFrozen")
    public ServerResponse updateFrozen() {
        this.simpleQuartzService.updateFrozen();
        return ServerResponse.createBySuccess();
    }

    /**
     * 记录学生当月的金币、勋章、膜拜次数排行信息
     *
     * @return
     */
    @PostMapping("/updateClassMonthRank")
    public ServerResponse<Object> updateClassMonthRank() {
        simpleQuartzService.updateClassMonthRank();
        return ServerResponse.createBySuccess();
    }

    @PostMapping("/updateRank")
    public ServerResponse<Object> updateRank() {
        simpleQuartzService.updateRank();
        return ServerResponse.createBySuccess();
    }

    @GetMapping("/updateStudentExpansion")
    public void updateStudentExpansion() {
        simpleQuartzService.updateStudentExpansion();
    }

    @PostMapping("/deleteSessionMap")
    public void deleteSessionMap() {
        simpleQuartzService.deleteSessionMap();
    }

    /**
     * 删除学生日奖励信息
     */
    @PostMapping("/deleteDailyAward")
    public void deleteDailyAward() {
        this.simpleQuartzService.deleteDailyAward();
    }

    /**
     * 删除抽奖基数
     */
    @PostMapping("/deleteDrawRedis")
    public void deleteDrawRedis() {
        this.simpleQuartzService.deleteDrawRedis();
    }

    /**
     * 初始化所有学生排行缓存信息
     *
     * @return
     */
    @GetMapping("/initRankCaches")
    public ServerResponse initRankCaches() {
        simpleQuartzService.initRankCaches();
        return ServerResponse.createBySuccess();
    }

    /**
     * 初始化所有指定学生排行缓存信息
     *
     * @return
     */
    @GetMapping("/initRankCache")
    public ServerResponse initRankCache(Long studentId) {
        simpleQuartzService.initRankCache(studentId);
        return ServerResponse.createBySuccess();
    }

    /**
     * 每周一凌晨删除学生定位信息
     *
     * @return
     */
    @PostMapping("/deleteStudentLocation")
    public ServerResponse deleteStudentLocation() {
        this.simpleQuartzService.deleteStudentLocation();
        return ServerResponse.createBySuccess();
    }
}
