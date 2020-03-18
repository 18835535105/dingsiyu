package com.zhidejiaoyu.common.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/18 09:39:39
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class PkCopyStateMapperTest {

    @Resource
    private PkCopyStateMapper pkCopyStateMapper;

    @Test
    public void selectById() {
        pkCopyStateMapper.selectById(1);
    }

}
