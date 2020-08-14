package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.ZdjyFrontApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: 76339
 * @date: 2020/8/14 20:52:52
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class StudentStudyPlanNewMapperTest {

    @Resource
    private StudentStudyPlanNewMapper studentStudyPlanNewMapper;

    @Test
    public void selectByStudentIdAndEasyOrHard() {
        studentStudyPlanNewMapper.selectByStudentIdAndEasyOrHard(1234L, 4);
    }
}
