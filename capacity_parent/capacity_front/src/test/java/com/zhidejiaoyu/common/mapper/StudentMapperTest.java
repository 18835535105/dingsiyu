package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.dto.wechat.qy.fly.SearchStudentDTO;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Slf4j
public class StudentMapperTest extends BaseTest {

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void testSelectByPrimaryKey() {
        System.out.println(studentMapper.selectById(7846L).toString());
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

    @Test
    public void selectByTeacherIdOrSchoolAdminId() {
        List<Student> students = studentMapper.selectByTeacherIdOrSchoolAdminId(561, new SearchStudentDTO());
        log.info("student={}", students.toString());
    }
}
