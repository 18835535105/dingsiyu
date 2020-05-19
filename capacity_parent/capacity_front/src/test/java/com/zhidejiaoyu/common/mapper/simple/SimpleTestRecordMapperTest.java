package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.ZdjyFrontApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/5/19 09:05:05
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class SimpleTestRecordMapperTest {

    @Resource
    private SimpleTestRecordMapper simpleTestRecordMapper;

    @Test
    public void selectByStudentIdAndUnitId() {
        simpleTestRecordMapper.selectByStudentIdAndUnitId(7846L, 123L, null, null);
    }

}
