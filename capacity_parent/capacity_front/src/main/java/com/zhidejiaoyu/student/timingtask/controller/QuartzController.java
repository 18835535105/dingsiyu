package com.zhidejiaoyu.student.timingtask.controller;


import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.timingtask.service.QuartzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <br>
 * <a href="https://www.showdoc.cc/65694455382333?page_id=2858504713316437">定时任务说明文档</a>
 */
@RestController
@RequestMapping("/api/quartz")
public class QuartzController {

    @Autowired
    private QuartzService quartzService;

    /**
     * 更新题型消息中学生账号到期提醒
     *
     * @return
     */
    @PostMapping("/updateNews")
    public ServerResponse<Object> updateNews() {
        quartzService.updateNews();
        return ServerResponse.createBySuccess();
    }

    /**
     * 学生能量清零
     *
     * @return
     */
    @PostMapping("/updateEnergy")
    public ServerResponse<Object> updateEnergy() {
        this.quartzService.updateEnergy();
        return ServerResponse.createBySuccess();
    }

    /**
     * 冻结的学生每日增加有效期
     *
     * @return
     */
    @PostMapping("/updateFrozen")
    public ServerResponse<Object> updateFrozen() {
        this.quartzService.updateFrozen();
        return ServerResponse.createBySuccess();
    }

    /**
     * 记录学生当月的金币、勋章、膜拜次数排行信息
     *
     * @return
     */
    @PostMapping("/updateClassMonthRank")
    public ServerResponse<Object> updateClassMonthRank() {
        quartzService.updateClassMonthRank();
        return ServerResponse.createBySuccess();
    }

    @PostMapping("/updateRank")
    public ServerResponse<Object> updateRank() {
        quartzService.updateRank();
        return ServerResponse.createBySuccess();
    }

    @GetMapping("/updateStudentExpansion")
    public void updateStudentExpansion() {
        quartzService.updateStudentExpansion();
    }

    @PostMapping("/deleteSessionMap")
    public void deleteSessionMap() {
        quartzService.deleteSessionMap();
    }


    /**
     * 删除抽奖基数
     */
    @PostMapping("/deleteDrawRedis")
    public void deleteDrawRedis() {
        this.quartzService.deleteDrawRedis();
    }

    /**
     * 删除所有到期60天的体验账号
     *
     * @return
     */
    @PostMapping("/deleteExperienceAccount")
    public ServerResponse<Object> deleteExperienceAccount() {
        quartzService.deleteExperienceAccount();
        return ServerResponse.createBySuccess();
    }

    /**
     * 每周一凌晨删除学生定位信息
     *
     * @return
     */
    @PostMapping("/deleteStudentLocation")
    public ServerResponse<Object> deleteStudentLocation() {
        this.quartzService.deleteStudentLocation();
        return ServerResponse.createBySuccess();
    }

    /**
     * 每天将过期的体验站好放入回收站中
     *
     * @return
     */
    @PostMapping("/saveRecycleBin")
    public ServerResponse<Object> saveRecycleBin() {
        this.quartzService.saveRecycleBin();
        return ServerResponse.createBySuccess();
    }

    /**
     * 将招生账号置为过期状态，一次性任务
     *
     * @return
     */
    @PostMapping("/updateWelfareAccountToOutOfDate")
    public ServerResponse<Object> updateWelfareAccountToOutOfDate() {
        this.quartzService.updateWelfareAccountToOutOfDate();
        return ServerResponse.createBySuccess();
    }

}
