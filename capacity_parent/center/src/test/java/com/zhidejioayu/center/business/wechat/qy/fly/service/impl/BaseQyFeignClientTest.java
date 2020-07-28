package com.zhidejioayu.center.business.wechat.qy.fly.service.impl;

import com.zhidejiaoyu.common.pojo.CurrentDayOfStudy;
import com.zhidejioayu.center.CenterApplication;
import com.zhidejioayu.center.business.feignclient.qy.BaseQyFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/29 19:57:57
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
public class BaseQyFeignClientTest {

//    @Resource
    private BaseQyFeignClient server1QyAuthFeignClient;

    public void testLogin() {
    }

//    @Test
    public void testCheckUpload() {
        boolean flag = server1QyAuthFeignClient.checkUpload("f72f0e5406c24f0d86b07c41ff7d44551592810389824");
        log.info("flag={}", flag);
    }

//    @Test
    public void testSave() {
        boolean b = server1QyAuthFeignClient.saveOrUpdateCurrentDayOfStudy(CurrentDayOfStudy.builder()
                .createTime(new Date())
                .imgUrl("111")
                .qrCodeNum(1)
                .studentId(7846L)
                .build());
        log.info("b={}", b);
    }

//    @Test
    public void testGetStudents() {
        Date date = new Date();
        Instant instant = date.toInstant();

        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime localDateTime = instant.atZone(zoneId).toLocalDateTime();

        LocalDateTime monday = localDateTime.with(TemporalAdjusters.previous(DayOfWeek.SUNDAY)).plusDays(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime sunday = localDateTime.with(TemporalAdjusters.next(DayOfWeek.MONDAY)).minusDays(1).withHour(23).withMinute(59).withSecond(59);
        ZonedDateTime zdt = monday.atZone(zoneId);//Combines this date-time with a time-zone to create a  ZonedDateTime.
        ZonedDateTime sdt = sunday.atZone(zoneId);
        System.out.println(Date.from(zdt.toInstant()));
        System.out.println(Date.from(sdt.toInstant()));
    }
}
