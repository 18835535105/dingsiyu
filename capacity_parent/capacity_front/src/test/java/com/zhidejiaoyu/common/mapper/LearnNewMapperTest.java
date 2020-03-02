package com.zhidejiaoyu.common.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/1/2 15:07:07
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class LearnNewMapperTest {

    @Resource
    private LearnNewMapper learnNewMapper;

    @Test
    public void selectById() {
        learnNewMapper.selectById(150L);
    }

    @Test
    public void selectDelLearnIdByStudentIdAndNumber() {
        learnNewMapper.selectDelLearnIdByStudentIdAndNumber(9604L, 1);
    }

    @Test
    public void countLearnedWordCountByStartDateAndEndDate() {
        learnNewMapper.countLearnedWordCountByStartDateAndEndDate(9604L, "2020-01-01", "2020-04-04");
    }
}
