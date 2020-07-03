package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.ZdjyFrontApplication;
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
 * @date: 2020/6/28 09:44:44
 */
@Slf4j
@SpringBootTest(classes = ZdjyFrontApplication.class)
@RunWith(SpringRunner.class)
public class CurrentDayOfStudyMapperTest{

    @Resource
    private CurrentDayOfStudyMapper currentDayOfStudyMapper;

    @Test
    public void testCountByStudentIdsAndDate() {
        List<Long> objects = new ArrayList<>();
        objects.add(7846L);
        objects.add(9319L);
        Map<Long, Map<Long, Long>> longMapMap = currentDayOfStudyMapper.countByStudentIdsAndDate(objects, null);
        log.info("longMapMap={}", longMapMap);
    }
}
