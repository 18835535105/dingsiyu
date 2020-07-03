package com.zhidejiaoyu.student.business.feignclient.center;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.pojo.center.ServerConfig;
import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 17:41:41
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class ServerConfigFeignClientTest  {

    @Resource
    private ServerConfigFeignClient serverConfigFeignClient;

    @Test
    public void testGetByServerNo() {
        ServerConfig byServerNo = serverConfigFeignClient.getByServerNo("2972466fd794414e83ddacefb7b78c8f1592189768879");
        log.info("serverconfig={}", byServerNo.toString());
    }
}
