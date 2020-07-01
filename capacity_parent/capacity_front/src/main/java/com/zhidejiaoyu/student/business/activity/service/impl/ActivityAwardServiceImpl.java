package com.zhidejiaoyu.student.business.activity.service.impl;

import com.zhidejiaoyu.common.constant.redis.WeekActivityRedisKeysConst;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.WeekActivity;
import com.zhidejiaoyu.common.pojo.WeekActivityConfig;
import com.zhidejiaoyu.common.rank.WeekActivityRankOpt;
import com.zhidejiaoyu.common.utils.TeacherInfoUtil;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.page.PageUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.business.activity.service.ActivityAwardService;
import com.zhidejiaoyu.student.business.activity.vo.AwardListVO;
import com.zhidejiaoyu.student.business.activity.vo.RankVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.timingtask.service.impl.QuartWeekActivityServiceImpl;
import com.zhidejiaoyu.student.common.redis.WeekActivityRedisOpt;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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

    @Resource
    private WeekActivityRankOpt weekActivityRankOpt;

    @Resource
    private StudentMapper studentMapper;

    @Override
    public ServerResponse<Object> awardList() {
        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        if (weekActivityConfig == null) {
            return ServerResponse.createBySuccess();
        }

        WeekActivity weekActivity = weekActivityMapper.selectById(weekActivityConfig.getWeekActivityId());
        long countDown = (weekActivityConfig.getActivityDateEnd().getTime() - System.currentTimeMillis()) / 1000;

        AwardListVO.AwardListVOBuilder awardListVoBuilder = AwardListVO.builder()
                .countDown(Math.max(0, countDown))
                .imgUrl(null)
                .name(weekActivity.getName());

        Integer id = weekActivity.getId();
        switch (id) {
            case 1:
                // 完成熟词
                return ServerResponse.createBySuccess(awardListVoBuilder.activityList(this.finishKnownWordsAward(weekActivity, weekActivityConfig)).build());
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

    @Override
    public ServerResponse<Object> rank(Integer type) {

        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        if (weekActivityConfig == null) {
            return ServerResponse.createBySuccess();
        }

        Student student = super.getStudent();

        if (type == 1) {
            // 校区排行
            return this.getSchoolRank(weekActivityConfig, student);
        }

        // 同服务器排行
        return this.getServerRank(weekActivityConfig, student);
    }

    public ServerResponse<Object> getServerRank(WeekActivityConfig weekActivityConfig, Student student) {
        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SERVER_RANK;
        List<Long> studentIds = weekActivityRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, 0L, 30L);

        Map<Long, Map<Long, String>> studentIdMap = studentMapper.selectNicknameMapByStudentId(studentIds);

        int size = studentIds.size();
        List<RankVO.RankInfo> vos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            RankVO.RankInfo info = new RankVO.RankInfo();
            Long studentId = studentIds.get(i);
            double activityPlan = weekActivityRankOpt.getActivityServerPlan(studentId);
            info.setNickname(studentIdMap.get(studentId).get("nickname"));
            if (i < 10) {
                Integer awardGold = QuartWeekActivityServiceImpl.SERVER_AWARD_GOLD_MAP.get(i);
                info.setAwardGold(awardGold == null ? 0 : awardGold);
            } else {
                info.setAwardGold(0);
            }
            info.setMe(Objects.equals(studentId, student.getId()));
            info.setComplete(WeekActivityRedisOpt.CONDITION_MAP.get(weekActivityConfig.getWeekActivityId()).replace("$&$", String.valueOf((int) activityPlan)));
            vos.add(info);
        }

        long memberSize = weekActivityRankOpt.getMemberSize(key);
        return ServerResponse.createBySuccess(PageUtil.packagePage(vos, memberSize));
    }

    /**
     * 获取校区排行
     *
     * @param weekActivityConfig
     * @param student
     * @return
     */
    public ServerResponse<Object> getSchoolRank(WeekActivityConfig weekActivityConfig, Student student) {

        int pageNum = PageUtil.getPageNum();
        int pageSize = PageUtil.getPageSize();
        long startIndex = (pageNum - 1) * pageSize;
        long endIndex = startIndex + pageSize;

        Integer schoolAdminId = TeacherInfoUtil.getSchoolAdminId(student);

        String key = WeekActivityRedisKeysConst.WEEK_ACTIVITY_SCHOOL_RANK + schoolAdminId;
        List<Long> studentIds = weekActivityRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, startIndex, endIndex);

        if (CollectionUtils.isEmpty(studentIds)) {
           return ServerResponse.createBySuccess(PageUtil.packagePage(Collections.emptyList(), 0L));
        }

        Map<Long, Map<Long, String>> studentIdMap = studentMapper.selectNicknameMapByStudentId(studentIds);

        int size = studentIds.size();
        List<RankVO.RankInfo> vos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            RankVO.RankInfo info = new RankVO.RankInfo();
            Long studentId = studentIds.get(i);
            double activityPlan = weekActivityRankOpt.getActivitySchoolPlan(schoolAdminId, studentId);
            info.setNickname(studentIdMap.get(studentId).get("nickname"));
            if (i < 10) {
                Integer awardGold = QuartWeekActivityServiceImpl.SERVER_AWARD_GOLD_MAP.get(i);
                info.setAwardGold(awardGold == null ? 0 : awardGold  / 2);
            } else {
                info.setAwardGold(0);
            }
            info.setMe(Objects.equals(studentId, student.getId()));
            info.setComplete(WeekActivityRedisOpt.CONDITION_MAP.get(weekActivityConfig.getWeekActivityId()).replace("$&$", String.valueOf((int) activityPlan)));
            vos.add(info);
        }

        long memberSize = weekActivityRankOpt.getMemberSize(key);
        return ServerResponse.createBySuccess(PageUtil.packagePage(vos, memberSize));
    }

    @Override
    public ServerResponse<Object> getAward(Integer awardGold) {
        Student student = super.getStudent();

        List<AwardListVO.ActivityList> activityList = this.getActivityLists(student);
        if (activityList == null) {
            return ServerResponse.createByError(300, "本周没有活动!");
        }

        for (AwardListVO.ActivityList list : activityList) {
            if (Objects.equals(list.getAwardGold(), awardGold)) {
                list.setCanGet(3);
            }
        }

        weekActivityRedisOpt.updateAwardList(student.getId(), activityList);
        return ServerResponse.createBySuccess();
    }

    public List<AwardListVO.ActivityList> getActivityLists(Student student) {
        WeekActivityConfig weekActivityConfig = weekActivityConfigMapper.selectCurrentWeekConfig();
        if (weekActivityConfig == null) {
            return null;
        }
        WeekActivity weekActivity = weekActivityMapper.selectById(weekActivityConfig.getWeekActivityId());

        return weekActivityRedisOpt.getActivityList(student.getId(), 0, weekActivity);
    }

    @Override
    public ServerResponse<Object> getAwardCount() {
        Student student = super.getStudent();

        List<AwardListVO.ActivityList> activityList = this.getActivityLists(student);
        if (activityList == null) {
            return ServerResponse.createByError(300, "本周没有活动!");
        }

        long count = activityList.stream().filter(activityList1 -> Objects.equals(activityList1.getCanGet(), 2)).count();

        Map<String, Long> map = new HashMap<>(16);
        map.put("count", count);

        return ServerResponse.createBySuccess(map);
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
        Integer bossHurt = bossHurtMapper.selectHurtNumByBeginDateAndEndDate(studentId,
                weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, bossHurt, weekActivity);
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
    private List<AwardListVO.ActivityList> finishKnownWordsAward(WeekActivity weekActivity, WeekActivityConfig weekActivityConfig) {
        Long studentId = super.getStudentId();
        int count = knownWordsMapper.countByStudentIdThisWeek(studentId, weekActivityConfig.getActivityDateBegin(), weekActivityConfig.getActivityDateEnd());
        return weekActivityRedisOpt.getActivityList(studentId, count, weekActivity);
    }

}
