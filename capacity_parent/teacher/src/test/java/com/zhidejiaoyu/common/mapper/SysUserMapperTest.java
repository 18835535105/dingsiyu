package com.zhidejiaoyu.common.mapper;

import com.dfdz.teacher.TeacherApplication;
import com.zhidejiaoyu.common.pojo.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author: wuchenxi
 * @date: 2020/8/4 10:07:07
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TeacherApplication.class)
public class SysUserMapperTest {

    @Resource
    public SysUserMapper sysUserMapper;

    @Test
    public void selectByOpenId() {
        SysUser sysUser = sysUserMapper.selectByOpenId("12312");
        log.info("sysUser={}", sysUser.toString());
    }
}
