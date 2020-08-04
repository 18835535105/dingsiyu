package com.zhidejiaoyu.common.mapper.center;

import com.zhidejiaoyu.common.pojo.center.BusinessUserInfo;
import com.zhidejioayu.center.CenterApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/8/4 10:02:02
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CenterApplication.class)
public class BusinessUserInfoMapperTest {

    @Resource
    private BusinessUserInfoMapper businessUserInfoMapper;

    @Test
    public void selectTeacherInfoByOpenid() {
        BusinessUserInfo businessUserInfo = businessUserInfoMapper.selectTeacherInfoByOpenid("12312");
        log.info("businessUserInfo={}", businessUserInfo.toString());
    }
}
