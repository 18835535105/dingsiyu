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
     * 排行名次与奖励的金币对应关系
     */
    private static final Map<Integer, Integer> AWARD_GOLD_MAP = new HashMap<>(16);

    static {
        AWARD_GOLD_MAP.put(1, 500);
        AWARD_GOLD_MAP.put(2, 300);
        AWARD_GOLD_MAP.put(3, 200);
        AWARD_GOLD_MAP.put(4, 100);
        AWARD_GOLD_MAP.put(5, 100);
        AWARD_GOLD_MAP.put(6, 50);
        AWARD_GOLD_MAP.put(7, 50);
        AWARD_GOLD_MAP.put(8, 50);
        AWARD_GOLD_MAP.put(9, 50);
        AWARD_GOLD_MAP.put(10, 50);
    }

    @Override
    @Scheduled(cron = "0 0 0 ? * 2 ")
    @Transactional(rollbackFor = Exception.class)
    public void init() {

        if (checkPort(port)) {
            return;
        }
        List<Long> schoolAdmins = teacherMapper.selectAllAdminId();

        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        Date createTime = new Date();

        log.info("初始化每周活动排行开始....");
        schoolAdmins.parallelStream().filter(Objects::nonNull).forEach(schoolAdmin -> {
            String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdmin;

            // 校区每周活动排行前十名奖励发放
            List<Long> betterTenStudentIds = weekActivityRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, 0L, 10L, 10);
            if (CollectionUtils.isNotEmpty(betterTenStudentIds)) {
                int size = betterTenStudentIds.size();
                for (int i = 0; i < size; i++) {
                    Student student = studentMapper.selectById(betterTenStudentIds.get(i));
                    Integer awardGold = AWARD_GOLD_MAP.get(i + 1);
                    student.setSystemGold(BigDecimalUtil.add(student.getSystemGold(), awardGold));
                    studentMapper.updateById(student);

                    GoldLogUtil.saveStudyGoldLog(student.getId(), GoldLogReasonConstant.WEEK_ACTIVITY_RANK, awardGold);
                }
            }

            // 保存总排行数据
            int schoolAdminId = Math.toIntExact(schoolAdmin);
            List<Student> students = studentMapper.selectNotDeleteBySchoolAdminId(schoolAdminId);
            if (CollectionUtils.isNotEmpty(students)) {
                List<WeekActivityRank> collect = students.parallelStream().map(student -> {
                    Double score = redisTemplate.opsForZSet().score(key, student.getId());

                    return WeekActivityRank.builder()
                            .complateCount(score == null ? 0 : (int) Math.round(score))
                            .createTime(createTime)
                            .nickName(student.getNickname())
                            .studentId(student.getId())
                            .weekActivityConfigId(weekActivityConfig.getId())
                            .build();
                }).collect(Collectors.toList());

                weekActivityRankService.saveBatch(collect);
            }

            // 初始化排行数据
            List<Long> studentIds = students.parallelStream().map(Student::getId).collect(Collectors.toList());
            redisTemplate.opsForZSet().remove(key, studentIds);
            weekActivityRankOpt.init(schoolAdminId, studentIds);
        });

        // 清空学生上个活动完成进度缓存
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_LIST;
        redisTemplate.opsForHash().delete(key);

        // 清空熟词表数据
        knownWordsMapper.delete(null);

        log.info("初始化每周活动排行结束.");

    }
}
