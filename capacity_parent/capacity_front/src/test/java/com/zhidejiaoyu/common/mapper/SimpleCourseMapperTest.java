package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2018-12-15
 */
@Slf4j
public class SimpleCourseMapperTest extends BaseTest {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void testSelectVersionByStudent() {
        Student student = studentMapper.selectById(7846);
        List<Map<String, Object>> strings = courseMapper.selectVersionByStudent(student);
        log.info(strings.toString());
    }

    @Test
    public void testSelectCourseByVersion() {
        Student student = studentMapper.selectById(7846);
        List<Map<String, Object>> mapList = courseMapper.selectCourseByVersion(student, "人教版");
        log.info(mapList.toString());
    }
}