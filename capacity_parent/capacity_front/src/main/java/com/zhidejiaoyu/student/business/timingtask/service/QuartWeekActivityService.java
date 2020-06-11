package com.zhidejiaoyu.student.business.timingtask.service;

/**
 * @author: wuchenxi
 * @date: 2020/5/27 10:27:27
 */
public interface QuartWeekActivityService {

    /**
     * 每周一0点5分奖励前10名学生并初始化校区活动排行
     */
    void init();
}
