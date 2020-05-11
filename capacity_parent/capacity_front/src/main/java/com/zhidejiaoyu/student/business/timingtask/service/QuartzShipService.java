package com.zhidejiaoyu.student.business.timingtask.service;

public interface QuartzShipService {


    /**
     * 每日添加修改解锁信息
     */
    void weekUnclock();

    /**
     * 每周解锁添加
     */
    void totalUnclock();

    void deleteSchoolCopy();
}
