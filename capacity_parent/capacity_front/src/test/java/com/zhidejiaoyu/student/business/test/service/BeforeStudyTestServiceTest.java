package com.zhidejiaoyu.student.business.test.service;

import com.zhidejiaoyu.ZdjyFrontApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: 76339
 * @date: 2020/8/19 15:11:11
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class BeforeStudyTestServiceTest {

    @Resource
    private BeforeStudyTestService beforeStudyTestService;

//    @Test
//    void fix() {
//        beforeStudyTestService.fix();
//    }
}
