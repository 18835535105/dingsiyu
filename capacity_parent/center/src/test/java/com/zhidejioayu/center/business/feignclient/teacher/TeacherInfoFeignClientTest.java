package com.zhidejioayu.center.business.feignclient.teacher;

import com.zhidejiaoyu.common.pojo.SysUser;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejioayu.center.CenterApplication;
import com.zhidejioayu.center.business.feignclient.util.FeignClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: 76339
 * @date: 2020/7/27 13:39:39
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
public class TeacherInfoFeignClientTest {

    @Test
    public void getByUuid() {
        BaseTeacherInfoFeignClient server1 = FeignClientUtil.getTeacherInfoFeignClient("server1");
        ServerResponse<SysUser> byUuid = server1.getByUuid("94cc7b7d0bd6495f90b8e149ef0aa5371593593648633");
        log.info("sysuser={}", byUuid.toString());
    }
}
