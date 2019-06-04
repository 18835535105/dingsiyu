package com.zhidejiaoyu;

import com.zhidejiaoyu.app.ZdjyFrontApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wuchenxi
 * @date 2018/7/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class BaseTest {

    @Test
    public void say() {
        System.out.println("hello".hashCode());
    }
}
