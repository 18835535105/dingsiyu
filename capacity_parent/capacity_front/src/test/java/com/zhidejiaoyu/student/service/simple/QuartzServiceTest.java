package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.student.timingtask.service.QuartzService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2019-06-21
 */
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
}
