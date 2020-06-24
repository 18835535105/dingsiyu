package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.constant.GoldLogReasonConstant;
import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import com.zhidejiaoyu.common.mapper.KnownWordsMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.mapper.WeekActivityConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.WeekActivityConfig;
import com.zhidejiaoyu.common.pojo.WeekActivityRank;
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.student.business.activity.service.WeekActivityRankService;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartWeekActivityService;
import com.zhidejiaoyu.student.common.GoldLogUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    private WeekActivityConfigMapper weekActivityConfigMapper;

    @Resource
    private WeekActivityRankService weekActivityRankService;

    @Resource
    private KnownWordsMapper knownWordsMapper;

    /**
     * 同服务器排行名次与奖励的金币对应关系，校区排名奖励金币数是该奖励的一半
     */
    public static final Map<Integer, Integer> SERVER_AWARD_GOLD_MAP = new HashMap<>(16);

    static {
        SERVER_AWARD_GOLD_MAP.put(0, 500);
        SERVER_AWARD_GOLD_MAP.put(1, 300);
        SERVER_AWARD_GOLD_MAP.put(2, 200);
        SERVER_AWARD_GOLD_MAP.put(3, 100);
        SERVER_AWARD_GOLD_MAP.put(4, 100);
        SERVER_AWARD_GOLD_MAP.put(5, 50);
        SERVER_AWARD_GOLD_MAP.put(6, 50);
        SERVER_AWARD_GOLD_MAP.put(7, 50);
        SERVER_AWARD_GOLD_MAP.put(8, 50);
        SERVER_AWARD_GOLD_MAP.put(9, 50);
    }


    @Override
    @Scheduled(cron = "0 5 0 * * 1")
    @Transactional(rollbackFor = Exception.class)
    public void init() {

        if (checkPort(port)) {
            return;
        }

        boolean flag = false;
        Object o = redisTemplate.opsForValue().get(WeekActivityRedisKeysConst.LAST_ACTIVITY_CONFIG_ID);
        if (o != null && Integer.parseInt(o.toString()) != -1) {
            // 说明上周有活动
            flag = true;
        }

        Date createTime = new Date();

        initWeekActivityServerRank(flag, o, createTime);

        initWeekActivitySchoolRank(flag, o, createTime);

        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        if (weekActivityConfig != null) {
            redisTemplate.opsForValue().set(WeekActivityRedisKeysConst.LAST_ACTIVITY_CONFIG_ID, weekActivityConfig.getId());
        } else {
            redisTemplate.opsForValue().set(WeekActivityRedisKeysConst.LAST_ACTIVITY_CONFIG_ID, -1);
        }
        redisTemplate.expire(WeekActivityRedisKeysConst.LAST_ACTIVITY_CONFIG_ID, 7, TimeUnit.DAYS);

        // 清空学生上个活动完成进度缓存
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_LIST;
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        redisTemplate.opsForHash().delete(key, keys);

        // 清空熟词表数据
        knownWordsMapper.delete(null);
        log.info("初始化每周活动校区排行结束.");
    }

    /**
     * 初始化同服务器排行
     *
     * @param flag
     * @param o
     * @param createTime
     */
    public void initWeekActivityServerRank(boolean flag, Object o, Date createTime) {
        log.info("初始化每周活动同服务器排行。。。");
        String serverKey = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SERVER_RANK;
        List<Student> serverStudents = studentMapper.selectNotDelete();
        if (flag) {
            // 取出前10名需要奖励的学生id
            List<Long> serverBetterTenStudentIds = weekActivityRankOpt.getReverseRangeMembersBetweenStartAndEnd(serverKey, 0L, 10L, 10);
            if (CollectionUtils.isNotEmpty(serverBetterTenStudentIds)) {
                int size = serverBetterTenStudentIds.size();
                for (int i = 0; i < size; i++) {
                    Student student = studentMapper.selectById(serverBetterTenStudentIds.get(i));
                    int awardGold = SERVER_AWARD_GOLD_MAP.get(i);
                    student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
                    studentMapper.updateById(student);

                    GoldLogUtil.saveStudyGoldLog(student.getId(), GoldLogReasonConstant.WEEK_ACTIVITY_SERVER_RANK, awardGold);
                }
            }

            // 保存总排行数据
            if (CollectionUtils.isNotEmpty(serverStudents)) {
                List<WeekActivityRank> collect = serverStudents.parallelStream().map(student -> {
                    Double score = redisTemplate.opsForZSet().score(serverKey, student.getId());

                    return WeekActivityRank.builder()
                            .complateCount(score == null ? 0 : (int) Math.round(score))
                            .createTime(createTime)
                            .nickName(student.getNickname())
                            .studentId(student.getId())
                            .weekActivityConfigId(Integer.parseInt(o.toString()))
                            .type(2)
                            .build();
                }).collect(Collectors.toList());

                weekActivityRankService.saveBatch(collect);
            }
        }

        // 初始化排行数据
        List<Long> serverStudentIds = serverStudents.parallelStream().map(Student::getId).collect(Collectors.toList());
        redisTemplate.opsForZSet().remove(serverKey, serverStudentIds);
        weekActivityRankOpt.initServerRank(serverStudentIds);

        log.info("初始化每周活动同服务器排行结束");
    }

    /**
     * 初始化校区排行
     *
     * @param flag
     * @param o
     * @param createTime
     */
    public void initWeekActivitySchoolRank(boolean flag, Object o, Date createTime) {
        log.info("初始化每周活动校区排行开始....");
        List<Long> schoolAdmins = teacherMapper.selectAllAdminId();
        schoolAdmins.parallelStream().filter(Objects::nonNull).forEach(schoolAdmin -> {

            int schoolAdminId = Math.toIntExact(schoolAdmin);
            List<Student> students = studentMapper.selectNotDeleteBySchoolAdminId(schoolAdminId);
            if (CollectionUtils.isNotEmpty(students)) {
                // 校区下没有学生直接跳过
                return;
            }

            String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdmin;

            if (flag) {
                // 如果上周有活动，发放奖励并保存总排行；如果上周没有活动，不做处理
                // 校区每周活动排行前十名奖励发放
                List<Long> betterTenStudentIds = weekActivityRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, 0L, 10L, 10);
                if (CollectionUtils.isNotEmpty(betterTenStudentIds)) {
                    int size = betterTenStudentIds.size();
                    for (int i = 0; i < size; i++) {
                        Student student = studentMapper.selectById(betterTenStudentIds.get(i));
                        int awardGold = SERVER_AWARD_GOLD_MAP.get(i) / 2;
                        student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
                        studentMapper.updateById(student);

                        GoldLogUtil.saveStudyGoldLog(student.getId(), GoldLogReasonConstant.WEEK_ACTIVITY_RANK, awardGold);
                    }
                }

                // 保存总排行数据
                if (CollectionUtils.isNotEmpty(students)) {
                    List<WeekActivityRank> collect = students.parallelStream().map(student -> {
                        Double score = redisTemplate.opsForZSet().score(key, student.getId());

                        return WeekActivityRank.builder()
                                .complateCount(score == null ? 0 : (int) Math.round(score))
                                .createTime(createTime)
                                .nickName(student.getNickname())
                                .studentId(student.getId())
                                .weekActivityConfigId(Integer.parseInt(o.toString()))
                                .type(1)
                                .build();
                    }).collect(Collectors.toList());

                    weekActivityRankService.saveBatch(collect);
                }
            }

            // 初始化排行数据
            List<Long> studentIds = students.parallelStream().map(Student::getId).collect(Collectors.toList());
            redisTemplate.opsForZSet().remove(key, studentIds);
            weekActivityRankOpt.initSchoolRank(schoolAdminId, studentIds);
        });
    }
}
