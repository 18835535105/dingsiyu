package com.zhidejiaoyu.student.business.service.impl;

import com.zhidejiaoyu.student.business.service.TeksService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wuchenxi
 * @date: 2020/4/13 18:20:20
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TeksServiceImplTest {

    @Resource
    private TeksService teksService;

    @Test
    public void testGetList() {
        String[] split = "Cairo, 2 p. m..".split(" ");
        Map<String, Object> map = new HashMap<>(16);
        teksService.getList(split, map);
        log.info(map.toString());
    }
}
