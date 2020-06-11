package com.zhidejiaoyu.student.business.timingtask.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/11 09:36:36
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class QuartWeekActivityServiceTest {

    @Resource
    private QuartWeekActivityService quartWeekActivityService;

    @Test
    public void testInit() {
        quartWeekActivityService.init();
    }
}
