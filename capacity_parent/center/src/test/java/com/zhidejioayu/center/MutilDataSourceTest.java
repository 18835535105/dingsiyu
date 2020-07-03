package com.zhidejioayu.center;

import com.zhidejioayu.center.service.MutilDataSourceService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/6/19 12:10:10
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
public class MutilDataSourceTest {

    @Resource
    private MutilDataSourceService mutilDataSourceService;

    @Test
    public void selectAllPrimary() {
        mutilDataSourceService.getServerConfig();
    }

    @Test
    public void selectAllServer1() {
        mutilDataSourceService.getStudentById();
    }
}
