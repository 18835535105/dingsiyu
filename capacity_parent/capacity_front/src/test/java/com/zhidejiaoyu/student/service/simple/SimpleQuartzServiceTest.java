package com.zhidejiaoyu.student.service.simple;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2019-06-21
 */
public class SimpleQuartzServiceTest extends BaseTest {

    @Autowired
    private SimpleQuartzService simpleQuartzService;

    @Test
    public void initRankCache() {
        simpleQuartzService.initRankCaches();
    }
}
