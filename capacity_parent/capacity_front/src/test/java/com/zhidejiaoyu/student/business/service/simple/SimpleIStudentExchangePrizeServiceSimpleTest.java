package com.zhidejiaoyu.student.business.service.simple;

import com.zhidejiaoyu.ZdjyFrontApplication;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/9/9 14:50:50
 */
@SpringBootTest(classes = ZdjyFrontApplication.class)
@RunWith(SpringRunner.class)
public class SimpleIStudentExchangePrizeServiceSimpleTest {

    @Resource
    private SimpleIStudentExchangePrizeServiceSimple simpleIStudentExchangePrizeServiceSimple;

    @Test
    public void exportData() {
        simpleIStudentExchangePrizeServiceSimple.exportData();
    }
}
