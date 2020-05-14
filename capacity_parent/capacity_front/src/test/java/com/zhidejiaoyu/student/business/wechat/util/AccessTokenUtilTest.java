package com.zhidejiaoyu.student.business.wechat.util;

import com.zhidejiaoyu.ZdjyFrontApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: wuchenxi
 * @date: 2020/5/14 10:03:03
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class AccessTokenUtilTest {

    @Test
    public void testGetPublicAccountAccessToken() {
//        String publicAccountAccessToken = AccessTokenUtil.getPublicAccountAuthAccessToken();
//        log.info(publicAccountAccessToken);
    }
}
