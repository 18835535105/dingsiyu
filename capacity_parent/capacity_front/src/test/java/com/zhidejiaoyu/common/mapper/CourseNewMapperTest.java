package com.zhidejiaoyu.common.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/4/20 15:38:38
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CourseNewMapperTest {

    @Resource
    private CourseNewMapper courseNewMapper;

    @Test
    public void testCountUnitByIds() {
        List<Long> ids = new ArrayList<>();
        ids.add(103031L);
        Map<Long, Map<Long, Object>> longMapMap = courseNewMapper.countUnitByIds(ids, 5);
        log.info("map={}", longMapMap);
    }
}
