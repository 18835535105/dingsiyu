package com.zhidejiaoyu.student.business.feignclient.course;

import com.zhidejiaoyu.ZdjyFrontApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: 76339
 * @date: 2020/8/15 08:35:35
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class UnitFeignClientTest {

    @Resource
    private UnitFeignClient unitFeignClient;

    @Test
    public void getSyntaxUnitLikeJointName() {
        unitFeignClient.getSyntaxUnitLikeJointName("123");
    }
}
