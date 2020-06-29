package com.zhidejiaoyu.student.business.feignclient.center;

import com.zhidejiaoyu.ZdjyFrontApplication;
import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejiaoyu.common.utils.IdUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2020/6/28 18:35:35
 */
@Slf4j
@SpringBootTest(classes = ZdjyFrontApplication.class)
@RunWith(SpringRunner.class)
public class UserInfoFeignClientTest {

    @Resource
    private UserInfoFeignClient userInfoFeignClient;

    @Test
    public void testGetUserInfoByUserUuid() {
        BusinessUserInfo businessUserInfo = userInfoFeignClient.getUserInfoByUserUuid("f72f0e5406c24f0d86b07c41ff7d44551592810389824");
        log.info("businessUserInfo={}", businessUserInfo.toString());
    }

//    @Test
    public void testUpdateUserInfo() {
        BusinessUserInfo businessUserInfo = userInfoFeignClient.getUserInfoByUserUuid("f72f0e5406c24f0d86b07c41ff7d44551592810389824");
        businessUserInfo.setId(IdUtil.getId());
        Boolean aBoolean = userInfoFeignClient.updateUserInfo(businessUserInfo);
        log.info("updateResult={}", aBoolean);
    }

//    @Test
    public void testSaveUserInfo() {
        BusinessUserInfo businessUserInfo = BusinessUserInfo.builder()
                .account("dz002035")
                .createTime(new Date())
                .openid("1123")
                .password("123")
                .serverConfigId("server_012")
                .updateTime(new Date())
                .userUuid(IdUtil.getId())
                .id(IdUtil.getId())
                .build();
        Boolean aBoolean = userInfoFeignClient.saveUserInfo(businessUserInfo);
        log.info("saveResult={}", aBoolean);

    }
}
