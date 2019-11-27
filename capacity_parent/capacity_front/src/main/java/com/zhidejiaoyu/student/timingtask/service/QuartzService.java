package com.zhidejiaoyu.student.timingtask.service;

/**
 * <b>定时任务相关业务</b><br>
 * <p>学生升级，每年8月25日 23:59:59 分学生由低年级升至高年级</p>
 * <p>每日 23:59:59 对距离有效期还剩3天的学生进行消息提醒</p>
 * <p>每日 00:10:00 更新提醒消息中学生账号到期提醒</p>
 * <p>每天早上 01:00:00 更新日奖励</p>
 *
 * <br><b>排行榜相关定时任务</b>
 * <ul>
 * <li>全校日排行，每日 00:30:00 更新学生全校日排行记录</li>
 * <li>全国、全校周排行，每周一 00:30:00 更新学生全国周排行记录、全校周排行记录，周日 23:50:00 统计学生本周排行名次变化</li>
 * <li>全校月排行，每月1号 00:30:00 更新学生全校月排行记录，每月最后一天 23:45:00 统计学生本月排行名次变化</li>
 * </ul>
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
}
