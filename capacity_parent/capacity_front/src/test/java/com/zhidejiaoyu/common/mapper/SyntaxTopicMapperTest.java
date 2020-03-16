package com.zhidejiaoyu.common.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

/**
 * @author: wuchenxi
 * @date: 2020/3/13 09:17:17
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class SyntaxTopicMapperTest {

    @Resource
    private SyntaxTopicMapper syntaxTopicMapper;

    @Test
    public void selectSelectSyntaxByUnitId() {
        syntaxTopicMapper.selectSelectSyntaxByUnitId(57882L);
    }

    @Test
    public void selectById() {
        syntaxTopicMapper.selectById(57882L);
    }
}
