package com.zhidejiaoyu.common.utils.goldUtil;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: 76339
 * @date: 2020/8/27 09:48:48
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ZdjyFrontApplication.class)
public class GoldUtilTest {

    @Resource
    private StudentMapper studentMapper;

    @Test
    public void addSmallAppGold() {
        Student student = studentMapper.selectById(7846);
//        int i = GoldUtil.addSmallAppGold(student, 10.3D);
//        log.info("i={}", i);
        int i1 = GoldUtil.addSmallAppGold(student, 99);
        log.info("i1={}", i1);
    }
}
