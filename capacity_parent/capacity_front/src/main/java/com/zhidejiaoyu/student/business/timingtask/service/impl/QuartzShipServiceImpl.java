package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.mapper.PkCopyStateMapper;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzShipService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/19 09:35:35
 */
@Slf4j
@Service
public class QuartzShipServiceImpl implements QuartzShipService, BaseQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private PkCopyStateMapper pkCopyStateMapper;

    @Override
    @Scheduled(cron = "0 2 0 * * 1")
    public void deleteSchoolCopy() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时删除校区副本挑战状态开始。。。");

        pkCopyStateMapper.deleteSchoolCopy();

        log.info("定时删除校区副本挑战状态完成。");
    }
}
