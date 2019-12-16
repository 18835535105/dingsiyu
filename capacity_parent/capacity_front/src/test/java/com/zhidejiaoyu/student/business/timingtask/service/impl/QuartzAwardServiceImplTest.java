package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;

import javax.annotation.Resource;

public class QuartzAwardServiceImplTest extends BaseTest {

    @Resource
    private QuartzAwardServiceImpl quartzAwardService;

    @Test
    public void initRankCache() {
        quartzAwardService.initRankCaches();
    }

    @Test
    public void initMonsterMedal() {
        quartzAwardService.initMonsterMedal();
    }

    @Test
    public void deleteRank() {
        quartzAwardService.deleteRank();
    }
}
