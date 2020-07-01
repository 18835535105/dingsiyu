package com.zhidejiaoyu.common.rank;

import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.WeekActivityConfig;
import com.zhidejiaoyu.common.utils.BigDecimalUtil;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 校区每周活动排行操作类
 *
 * @author: wuchenxi
 * @date: 2020/5/27 10:20:20
 */
@Slf4j
@Component
public class WeekActivityRankOpt extends BaseRankOpt {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private WeekActivityConfigMapper weekActivityConfigMapper;

    @Resource
    private KnownWordsMapper knownWordsMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private GauntletMapper gauntletMapper;

    @Resource
    private ClockInMapper clockInMapper;

    @Resource
    private BossHurtMapper bossHurtMapper;


    /**
     * 初始化每周活动校区排行数据
     *
     * @param schoolAdminId
     * @param studentIds
     */
    public void initSchoolRank(Integer schoolAdminId, List<Long> studentIds) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdminId;
        initRank(studentIds, key);
    }

    /**
     * 初始化每周活动同服务器排行数据
     *
     * @param studentIds
     */
    public void initServerRank(List<Long> studentIds) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SERVER_RANK;
        initRank(studentIds, key);
    }

    public void initRank(List<Long> studentIds, String key) {
        if (CollectionUtils.isNotEmpty(studentIds)) {
            studentIds.parallelStream()
                    .forEach(studentId -> {
                        if (log.isDebugEnabled()) {
                            log.debug("初始化key：{}, 学生： {}", key, studentId);
                        }
                        redisTemplate.opsForZSet().add(key, studentId, 0);
                    });
        }
        redisTemplate.expire(key, 7, TimeUnit.DAYS);
    }

    /**
     * 更新排行
     *
     * @param schoolAdminId
     * @param studentId
     * @param score
     */
    private void updateRank(Integer schoolAdminId, Long studentId, double score) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdminId;
        redisTemplate.opsForZSet().add(key, studentId, score);

        String key1 = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SERVER_RANK;
        redisTemplate.opsForZSet().add(key1, studentId, score);
    }

    /**
     * 查询校区学生活动完成进度
     *
     * @param schoolAdminId
     * @param studentId
     * @return
     */
    public double getActivitySchoolPlan(Integer schoolAdminId, Long studentId) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdminId;
        Double score = redisTemplate.opsForZSet().score(key, studentId);
        return score == null ? 0.0 : score;
    }

    /**
     * 查询同服务器学生活动完成进度
     *
     * @param studentId
     * @return
     */
    public double getActivityServerPlan(Long studentId) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SERVER_RANK;
        Double score = redisTemplate.opsForZSet().score(key, studentId);
        return score == null ? 0.0 : score;
    }

    /**
     * 更新熟词排行
     *
     * @param student
     */
    public void updateWeekActivitySchoolRank(Student student) {
        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        if (weekActivityConfig == null) {
            return;
        }
        Integer weekActivityId = weekActivityConfig.getWeekActivityId();
        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);
        double score;
        Long studentId = student.getId();
        switch (weekActivityId) {
            case 1:
                // 完成熟词
                int count = knownWordsMapper.countByStudentIdThisWeek(studentId, weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
                score = count * 1.0;
                break;
            case 2:
                // 学习时长
                Integer validTime = durationMapper.selectValidTime(studentId,
                        DateUtil.formatDate(weekActivityConfig.getActivityDateBegin(), DateUtil.YYYYMMDDHHMMSS),
                        DateUtil.formatDate(weekActivityConfig.getActivityDateEnd(), DateUtil.YYYYMMDDHHMMSS));
                score = (validTime == null ? 0.0D : BigDecimalUtil.div(validTime * 1.0, 3600, 2));
                break;
            case 3:
                // 金币贡献
                Integer sum = goldLogMapper.selectGoldContributeByBeginTimeAndEndTime(studentId,
                        weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
                score = (sum == null ? 0.0D : sum * 1.0 / 10);
                break;
            case 4:
                // 成绩累计
                Integer totalPoint = testRecordMapper.selectTotalPointByBeginDateAndEndDate(studentId,
                        weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
                score = totalPoint == null ? 0 : totalPoint;
                break;
            case 5:
                // PK获胜累计数
                score = gauntletMapper.countWinCount(studentId,
                        weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
                break;
            case 6:
                // 金币考试
                Integer maxPoint = testRecordMapper.selectGoldTestMaxPointByBeginDateAndEndDate(studentId,
                        weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
                score = (maxPoint == null ? 0 : maxPoint);
                break;
            case 7:
                // 连续打卡
                Integer cardDays = clockInMapper.selectLaseCardDays(studentId);
                score = (cardDays == null ? 0 : cardDays);
                break;
            case 8:
                // boss伤害累计
                Integer bossHurt = bossHurtMapper.selectHurtNumByBeginDateAndEndDate(studentId,
                        weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
                score = (bossHurt == null ? 0 : bossHurt);
                break;
            default:
                throw new ServiceException("未匹配到id为 " + weekActivityId + " 的每周活动数据！");
        }
        this.updateRank(schoolAdminId, studentId, score);

    }

}
