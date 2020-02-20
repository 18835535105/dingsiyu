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

    @Test
    public void testSelectByOpenId() {
        studentMapper.selectByOpenId("oqSJe5X3KE8ojWyIbm9FTls-gh7U,oqSJe5TC6OWKrM3bDd8QyI8GdZb4");
    }
}
