package com.dfdz.course.business.service.impl;

import com.dfdz.course.business.service.UnitService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/7/16 14:49:49
 */
@SpringBootTest
@RunWith(SpringRunner.class)
class UnitServiceImplTest {

    @Resource
    private UnitService unitService;

    @Test
    void getMaxGroupByUnitIsdAndType() {
        List<Long> list = new ArrayList<>();
        list.add(101042L);
        unitService.getMaxGroupByUnitIsdAndType(list, 1);
    }
}
