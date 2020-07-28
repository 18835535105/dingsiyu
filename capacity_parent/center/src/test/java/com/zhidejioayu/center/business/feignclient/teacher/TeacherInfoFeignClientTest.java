package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejioayu.center.CenterApplication;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: 76339
 * @date: 2020/7/27 13:39:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
public class TeacherInfoFeignClientTest {

    @Test
    public void getByUuid() {
        BaseTeacherInfoFeignClient server1 = FeignClientUtil.getTeacherInfoFeignClient("server1");
        server1.getByUuid("123");
    }
}
