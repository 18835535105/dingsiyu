package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class StudentMapperTest extends BaseTest {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void testSelectByPrimaryKey() {
        System.out.println(studentMapper.selectByPrimaryKey(7846L).toString());
    }
}
