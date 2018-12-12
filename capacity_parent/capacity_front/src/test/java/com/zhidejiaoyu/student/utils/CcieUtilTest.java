package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author wuchenxi
 * @date 2018/8/28
 */
public class CcieUtilTest extends BaseTest {

    @Autowired
    private CcieUtil ccieUtil;

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void saveCcieTest() {
        Student student = studentMapper.selectByPrimaryKey(3155L);
        ccieUtil.saveCcieTest(student, 1, 1, 1);
    }
}