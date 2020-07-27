package com.zhidejiaoyu.common.mapper.center;

import com.zhidejioayu.center.CenterApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/7/27 11:11:11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
public class QaUnknownMapperTest {

    @Resource
    private QaUnknownMapper qaUnknownMapper;

    @Test
    public void countByQuestion() {
        int count = qaUnknownMapper.countByQuestion("不知道怎么办");
        log.info("count={}", count);
    }
}
