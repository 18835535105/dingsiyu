package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.ZdjyFrontApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/4/29 13:43:43
 */
@Slf4j
@SpringBootTest(classes = ZdjyFrontApplication.class)
@RunWith(SpringRunner.class)
public class StudentEquipmentMapperTest {

    @Resource
    private StudentEquipmentMapper studentEquipmentMapper;

    @Test
    public void testCountEquipmentShipByStudentId() {
        int i = studentEquipmentMapper.countEquipmentShipByStudentId(7846L);
        assert i == 1;
    }
}
