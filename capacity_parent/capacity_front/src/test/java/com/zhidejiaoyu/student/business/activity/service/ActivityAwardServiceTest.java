package com.zhidejiaoyu.student.business.activity.service;

import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/6/10 15:24:24
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class ActivityAwardServiceTest{

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private WeekActivityRankOpt weekActivityRankOpt;

    @Test
    public void initSchoolRank() {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + 561;

        List<Student> students = studentMapper.selectNotDeleteBySchoolAdminId(561);
        List<Long> studentIds = students.parallelStream().map(Student::getId).collect(Collectors.toList());
        redisTemplate.opsForZSet().remove(key, studentIds);
        weekActivityRankOpt.init(561, studentIds);
    }
}
