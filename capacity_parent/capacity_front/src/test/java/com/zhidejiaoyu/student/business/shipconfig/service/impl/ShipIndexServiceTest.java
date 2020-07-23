package com.zhidejiaoyu.student.business.shipconfig.service.impl;

import com.zhidejiaoyu.student.business.shipconfig.service.ShipIndexService;
import com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo.IndexVO;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/13 11:05:05
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShipIndexServiceTest {

    @Resource
    private ShipIndexService shipIndexService;

    @Test
    public void initRank() {
        shipIndexService.initRank();
    }

    @Test
    public void getStateOfWeek() {
        IndexVO.BaseValue stateOfWeek = shipIndexService.getStateOfWeek(9575L);
        log.info(stateOfWeek.toString());
    }
}
