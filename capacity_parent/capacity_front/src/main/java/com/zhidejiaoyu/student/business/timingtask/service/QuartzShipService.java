package com.zhidejiaoyu.student.business.timingtask.service;

/**
 * 飞船、pk相关定时任务
 *
 * @author: wuchenxi
 * @date: 2020/3/19 09:35:35
 */
public interface QuartzShipService {


    /**
     * 每周一00：02：00删除校区副本的挑战状态
     */
    void deleteSchoolCopy();
}
