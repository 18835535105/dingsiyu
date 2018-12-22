package com.zhidejiaoyu;

import com.zhidejiaoyu.app.ZdjyFrontApplication;
import com.zhidejiaoyu.common.utils.language.YouDaoTranslate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author wuchenxi
 * @date 2018/7/14
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class BaseTest {

    @Resource
    private YouDaoTranslate youDaoTranslate;

    @Test
    public void youdaoTest() {
        try {
            System.out.println(youDaoTranslate.getResultMap("word"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void say() {
        System.out.println("hello".hashCode());
    }
}
