package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.zhidejiaoyu.BaseTest;
import com.zhidejiaoyu.common.pojo.StudentFlowNew;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @author: wuchenxi
 * @date: 2019/12/25 14:37:37
 */
@Transactional
public class StudentFlowNewMapperTest extends BaseTest {

    @Resource
    private StudentFlowNewMapper studentFlowNewMapper;

    @Test
    public void test() {
        studentFlowNewMapper.update(StudentFlowNew.builder()
                .currentFlowId(50L)
                .updateTime(new Date())
                .build(), new EntityWrapper<StudentFlowNew>().eq("student_id", 9604)
                .eq("unit_id", 11111));
    }

    @Test
    public void deleteByStudentAndType() {
        studentFlowNewMapper.deleteByLearnId(12L);
    }
}
