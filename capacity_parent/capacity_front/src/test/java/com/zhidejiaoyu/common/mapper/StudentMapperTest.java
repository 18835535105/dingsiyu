package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
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

    @Test
    public void countBySchoolAdminId() {
        int i = studentMapper.countBySchoolAdminId(299);
        log.info("i={}", i);
    }
}
