package com.zhidejiaoyu.common.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/4/13 15:35:35
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class ClockInMapperTest {

    @Resource
    private ClockInMapper clockInMapper;

    @Test
    public void testCountByStudentId() {
        int count = clockInMapper.countByStudentId(7846L);
        log.info("count={}", count);
    }
}
