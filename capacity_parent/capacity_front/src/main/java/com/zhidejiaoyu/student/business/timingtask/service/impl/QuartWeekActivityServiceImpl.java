package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartWeekActivityService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author: wuchenxi
 * @date: 2020/5/27 10:27:27
 */
@Slf4j
@Service
public class QuartWeekActivityServiceImpl implements BaseQuartzService, QuartWeekActivityService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private TeacherMapper teacherMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private WeekActivityRankOpt weekActivityRankOpt;

    @Override
    @Scheduled(cron = "0 0 0 ? * 2 ")
    public void init() {

        if (checkPort(port)) {
            return;
        }
        log.info("初始化每周活动排行开始....");
        List<Long> schoolAdmins = teacherMapper.selectAllAdminId();

        schoolAdmins.parallelStream().filter(Objects::nonNull).forEach(schoolAdmin -> {
            String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdmin;
            int schoolAdminId = Math.toIntExact(schoolAdmin);
            List<Long> students = studentMapper.selectNotDeleteIdsBySchoolAdminId(schoolAdminId);
            redisTemplate.opsForZSet().remove(key, students);

            weekActivityRankOpt.init(schoolAdminId, students);
        });
        log.info("初始化每周活动排行结束.");

    }
}
