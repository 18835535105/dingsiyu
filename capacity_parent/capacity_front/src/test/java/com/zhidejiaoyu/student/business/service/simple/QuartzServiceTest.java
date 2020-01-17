package com.zhidejiaoyu.student.business.service.simple;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author wuchenxi
 * @date 2019-06-21
 */
@Transactional
public class QuartzServiceTest extends BaseTest {

    @Autowired
    private QuartzService quartzService;

    @Test
    public void testUpdateClassMonthRank() {
        quartzService.updateClassMonthRank();
    }

    @Test
    public void updateWelfareAccountToOutOfDate() {
        quartzService.updateWelfareAccountToOutOfDate();
    }

    @Test
    public void addStudyByWeek() {
        quartzService.addStudyByWeek();
    }

    @Test
    public void calculateRateOfChange() {
        quartzService.calculateRateOfChange();
    }

    @Test
    public void updateRank() {
        quartzService.updateRank();
    }
}
