package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudyCalendarService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/31 15:20:20
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QuartzStudyCalendarServiceImplTest {

    @Resource
    private QuartzStudyCalendarService quartzStudyCalendarService;

    @Test
    public void initLearningDetails() {
        quartzStudyCalendarService.initLearningDetails();
    }
}
