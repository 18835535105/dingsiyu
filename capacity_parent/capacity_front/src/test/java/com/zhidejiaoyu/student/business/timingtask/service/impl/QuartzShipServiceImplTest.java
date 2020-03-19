package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.student.business.timingtask.service.QuartzShipService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/19 09:42:42
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class QuartzShipServiceImplTest {

    @Resource
    private QuartzShipService quartzShipService;

    @Test
    public void deleteSchoolCopy() {
        quartzShipService.deleteSchoolCopy();
    }
}
