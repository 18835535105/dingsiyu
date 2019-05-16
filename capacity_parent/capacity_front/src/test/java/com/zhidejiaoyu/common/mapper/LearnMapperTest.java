package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.pojo.Learn;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2019-05-16
 */
@Slf4j
public class LearnMapperTest extends BaseTest {

    @Autowired
    private LearnMapper learnMapper;

    @Autowired
    private StudentMapper studentMapper;

    private Student student;

    @Before
    public void getStudent() {
        this.student = studentMapper.selectById(7846L);
    }

    @Test
    public void testSelTeksLearn() {
        Learn learn = new Learn();
        learn.setStudentId(student.getId());
        learn.setUnitId(48752L);
        learn.setCourseId(4996L);
        learn.setStudyModel("课文试听");
        Long learnId = learnMapper.selTeksLearn(learn);
        log.info("learnId=" + learnId);
    }

}
