package com.zhidejioayu.center.business.wechat.feignclient.qy;

import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejioayu.center.CenterApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 19:57:57
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
public class BaseQyFeignClientTest {

    @Resource
    private BaseQyFeignClient server1QyAuthFeignClient;

    public void testLogin() {
    }

    @Test
    public void testCheckUpload() {
        boolean flag = server1QyAuthFeignClient.checkUpload("f72f0e5406c24f0d86b07c41ff7d44551592810389824");
        log.info("flag={}", flag);
    }

    @Test
    public void testSave() {
        boolean b = server1QyAuthFeignClient.saveCurrentDayOfStudy(CurrentDayOfStudy.builder()
                .createTime(new Date())
                .imgUrl("111")
                .qrCodeNum(1)
                .studentId(7846L)
                .build());
        log.info("b={}", b);
    }

    public void testGetStudents() {
    }
}
