package com.zhidejiaoyu.student.timingtask.service;

/**
 * @author wuchenxi
 * <br>
 * <a href="https://www.showdoc.cc/65694455382333?page_id=2858504713316437">定时任务说明文档</a>
 * @date 2018/5/22 16:25
 */
public interface QuartzAwardService {

    /**
     * 删除学生日奖励信息
     */
    void deleteDailyAward();

    /**
     * 初始化排行缓存信息
     */
    void initRankCaches();

    /**
     * 初始化所有指定学生排行缓存信息
     *
     * @param studentId
     * @return
     */
    void initRankCache(Long studentId);

    /**
     * 初始化怪物勋章
     */
    void initMonsterMedal();

    /**
     * 定时删除多余的学生排行
     */
    void deleteRank();
}
