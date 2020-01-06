package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.Student;
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
 * @date: 2020/1/6 14:28:28
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnHistoryMapperTest {
    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private StudentMapper studentMapper;

    @Test
    public void countUnitByCourseIds() {
        Student student = studentMapper.selectById(13365L);

        List<Long> courseIds = new ArrayList<>();
        courseIds.add(9402L);

        Map<Long, Map<Long, Object>> longMapMap = learnHistoryMapper.countUnitByStudentIdAndCourseIds(student.getId(), courseIds, 1);
        log.info("longMapMap={}", longMapMap);
    }
}
