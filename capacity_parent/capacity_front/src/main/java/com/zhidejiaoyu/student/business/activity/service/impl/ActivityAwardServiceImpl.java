package com.zhidejiaoyu.student.business.activity.service.impl;

import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.WeekActivity;
import com.zhidejiaoyu.common.pojo.WeekActivityConfig;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.activity.service.ActivityAwardService;
import com.zhidejiaoyu.student.business.activity.vo.AwardListVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.common.redis.WeekActivityRedisOpt;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * @author: wuchenxi
 * @date: 2020/5/27 14:20:20
 */
@Service
public class ActivityAwardServiceImpl extends BaseServiceImpl<WeekActivityMapper, WeekActivity> implements ActivityAwardService {

    @Resource
    private WeekActivityConfigMapper weekActivityConfigMapper;

    @Resource
    private WeekActivityMapper weekActivityMapper;

    @Resource
    private KnownWordsMapper knownWordsMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private WeekActivityRedisOpt weekActivityRedisOpt;

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

    @Override
    public ServerResponse<Object> awardList() {
        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        if (weekActivityConfig == null) {
            return ServerResponse.createBySuccess();
        }

        WeekActivity weekActivity = weekActivityMapper.selectById(weekActivityConfig.getWeekActivityId());

        Date sunday = DateUtil.getDateOfWeekDay(7);
        Date parse = DateUtil.parse(DateUtil.formatYYYYMMDD(sunday) + " 15:00:00", DateUtil.YYYYMMDDHHMMSS);
        long countDown = parse == null ? 0L : (parse.getTime() - System.currentTimeMillis()) / 1000;

        AwardListVO.AwardListVOBuilder awardListVoBuilder = AwardListVO.builder()
                .countDown(Math.max(0, countDown))
                .imgUrl(null)
                .name(weekActivity.getName());

        Integer id = weekActivity.getId();
        switch (id) {
            case 1:
                // 完成熟词
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.finishKnownWordsAward(weekActivity)).build());
            case 2:
                // 学习时长
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.validTime(weekActivity, weekActivityConfig)).build());
            case 3:
                // 金币贡献
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.goldContribute(weekActivity, weekActivityConfig)).build());
            case 4:
                // 成绩累计
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.record(weekActivity, weekActivityConfig)).build());
            case 5:
                // PK获胜累计数
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.pkWinCount(weekActivity, weekActivityConfig)).build());
            case 6:
                // 金币考试
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.goldTest(weekActivity, weekActivityConfig)).build());
            case 7:
                // 连续打卡
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.card(weekActivity)).build());
            case 8:
                // boss伤害累计
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.bossHurt(weekActivity, weekActivityConfig)).build());
            default:
                throw new ServiceException("未匹配到id为 " + id + " 的每周活动数据！");
        }
    }

    /**
     * boss伤害累积值
     *
     * @param weekActivity
     * @param weekActivityConfig
     * @return
     */
    private List<AwardListVO.ActivityList> bossHurt(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        Integer cardDays = bossHurtMapper.selectHurtNumByBeginDateAndEndDate(studentId,
                weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, cardDays, weekActivity);
    }

    /**
     * 连续打卡天数
     *
     * @param weekActivity
     * @return
     */
    private List<AwardListVO.ActivityList> card(WeekActivity weekActivity) {
        Long studentId = super.getStudentId();
        Integer cardDays = clockInMapper.selectLaseCardDays(studentId);
        return weekActivityRedisOpt.getActivityList(studentId, cardDays, weekActivity);
    }

    /**
     * 金币测试分数
     *
     * @param weekActivity
     * @param weekActivityConfig
     * @return
     */
    private List<AwardListVO.ActivityList> goldTest(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        Integer maxPoint = testRecordMapper.selectGoldTestMaxPointByBeginDateAndEndDate(studentId,
                weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, maxPoint, weekActivity);
    }

    /**
     * PK获胜次数
     *
     * @param weekActivity
     * @param weekActivityConfig
     * @return
     */
    private List<AwardListVO.ActivityList> pkWinCount(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        Integer sum = gauntletMapper.countWinCount(studentId,
                weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, sum, weekActivity);
    }

    /**
     * 成绩类累计
     *
     * @param weekActivity
     * @param weekActivityConfig
     * @return
     */
    private List<AwardListVO.ActivityList> record(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        Integer sum = testRecordMapper.selectTotalPointByBeginDateAndEndDate(studentId,
                weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, sum, weekActivity);
    }

    /**
     * 学生总有效时长
     *
     * @param weekActivity
     * @param weekActivityConfig
     * @return
     */
    private List<AwardListVO.ActivityList> validTime(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        Integer validTime = durationMapper.selectValidTime(studentId,
                DateUtil.formatDate(weekActivityConfig.getActivityDateBegin(), DateUtil.YYYYMMDDHHMMSS),
                DateUtil.formatDate(weekActivityConfig.getActivityDateEnd(), DateUtil.YYYYMMDDHHMMSS));
        return weekActivityRedisOpt.getActivityList(studentId, validTime, weekActivity);
    }

    /**
     * 金币贡献
     *
     * @param weekActivity
     * @param weekActivityConfig
     * @return
     */
    private List<AwardListVO.ActivityList> goldContribute(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        Integer sum = goldLogMapper.selectGoldContributeByBeginTimeAndEndTime(studentId,
                weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, sum == null ? 0 : (int) (sum * 0.1), weekActivity);
    }

    /**
     * 完成熟词奖励
     *
     * @param weekActivity
     * @return
     */
    private List<AwardListVO.ActivityList> finishKnownWordsAward(WeekActivity weekActivity) {
        Long studentId = super.getStudentId();
        int count = knownWordsMapper.countByStudentId(studentId);
        return weekActivityRedisOpt.getActivityList(studentId, count, weekActivity);
    }


}
