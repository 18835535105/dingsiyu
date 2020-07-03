package com.zhidejiaoyu.common.mapper.simple;

import com.zhidejiaoyu.ZdjyFrontApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/7/3 15:16:16
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class SimpleStudentMapperTest {

    @Resource
    private SimpleStudentMapper simpleStudentMapper;

    @Test
    public void selectMaxSourceByClassId() {
        List<Long> longs = simpleStudentMapper.selectMaxSourceByClassId(null, 561L, null, null);
        log.info("longs={}", longs.toString());
    }

}
