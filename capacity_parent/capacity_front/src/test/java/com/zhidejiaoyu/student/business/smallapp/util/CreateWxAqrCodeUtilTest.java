package com.zhidejiaoyu.student.business.smallapp.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: wuchenxi
 * @date: 2020/2/20 11:18:18
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CreateWxAqrCodeUtilTest {

    @Test
    public void create() {
        CreateWxAqrCodeUtil.create("111", 120);
    }
}
