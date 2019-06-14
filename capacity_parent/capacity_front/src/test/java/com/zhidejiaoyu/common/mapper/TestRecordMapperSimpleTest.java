package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.app.ZdjyFrontApplication;
import com.zhidejiaoyu.common.Vo.testVo.TestDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author wuchenxi
 * @date 2018-12-27
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class TestRecordMapperSimpleTest {

    @Autowired
    private TestRecordMapper testRecordMapper;

    @Test
    public void testSelectTestDetailVo() {
        TestDetailVo testDetailVo = testRecordMapper.selectTestDetailVo(9985L, 9225L, 1);
        log.info("vo=[{}]", testDetailVo);
    }
}