package com.zhidejioayu.center.business.wechat.qy.fly.service.impl;

import com.zhidejioayu.center.business.wechat.qy.fly.service.QyFlyService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/24 17:12:12
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class QyFlyServiceImplTest {

    @Resource
    private QyFlyService qyFlyService;

    @Test
    public void testGetCurrentDayOfStudy() {
        qyFlyService.getCurrentDayOfStudy("f72f0e5406c24f0d86b07c41ff7d44551592810389824");
    }
}
