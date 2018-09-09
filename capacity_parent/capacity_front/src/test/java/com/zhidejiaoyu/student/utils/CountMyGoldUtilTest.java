package com.zhidejiaoyu.student.utils;

import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

/**
 * @author wuchenxi
 * @date 2018/8/27
 */
public class CountMyGoldUtilTest extends BaseTest {

    @Autowired
    private CountMyGoldUtil countMyGoldUtil;

    @Autowired
    private StudentMapper studentMapper;

    @Test
    public void countMyGold() {
        Student student =studentMapper.selectByPrimaryKey(3194L);
        countMyGoldUtil.countMyGold(student);
    }
}