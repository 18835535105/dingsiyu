package com.zhidejiaoyu.student.business.timingtask.service;

/**
 * <br>
 * <a href="https://www.showdoc.cc/65694455382333?page_id=2858504713316437">定时任务说明文档</a>
 *
 * @author wuchenxi
 * @date 2018/5/22 16:25
 */
public interface QuartzService {

    /**
     * 每日 00:10:00 更新提醒消息中学生账号到期提醒
     */
    void updateNews();

    /**
     * 00:30:00 更新排行榜:
     * <ul>
     * <li>每天更新全校日排行榜</li>
     * <li>每周一更新全校和全国周排行榜</li>
     * <li>每月一日更新全校月排行榜</li>
     * </ul>
     */
    void updateRank();

    /**
     * 00:20:00 更新能量:
     */
    void updateEnergy();

    /**
     * 每月 1 号 00：05：00 记录学生当月的金币、勋章、膜拜次数排行信息
     */
    void updateClassMonthRank();

    /**
     * 每天0：30给每一个冻结用户有效期增加一天
     */
    void updateFrozen();

    /**
     * 每天早上2：0：0执行
     */
    void deleteSessionMap();

    void deleteDrawRedis();

    void updateStudentExpansion();

    /**
     * 每周一 00：05 删除所有学生定位信息
     *
     * @return
     */
    void deleteStudentLocation();

    void deleteExperienceAccount();

    void saveRecycleBin();

    /**
     * 将招生账号置为过期状态，一次性任务
     *
     * @return
     */
    void updateWelfareAccountToOutOfDate();

    void CalculateRateOfChange();

    void addStudyByWeek();
}
