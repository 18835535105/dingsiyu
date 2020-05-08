package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.constant.redis.SourcePowerKeysConst;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
import com.zhidejiaoyu.common.rank.SourcePowerRankOpt;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudyCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/3/30 16:03:03
 */
@Slf4j
@Service
public class QuartzStudyCalendarServiceImpl implements QuartzStudyCalendarService {
    final String schoolMsg = "校区排行金币奖励";
    final String allMsg = "全国排行金币奖励";
    @Value("${quartz.port}")
    private int port;

    @Resource
    private TestRecordMapper testRecordMapper;

    @Resource
    private WorshipMapper worshipMapper;

    @Resource
    private GoldLogMapper goldLogMapper;

    @Resource
    private StudentDailyLearningMapper studentDailyLearningMapper;

    @Resource
    private ClockInMapper clockInMapper;

    @Resource
    private PunchRecordMapper punchRecordMapper;

    @Resource
    private RunLogMapper runLogMapper;

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private LearnNewMapper learnNewMapper;

    @Resource
    private LearnHistoryMapper learnHistoryMapper;

    @Resource
    private CourseNewMapper courseNewMapper;

    @Resource
    private UnitNewMapper unitNewMapper;

    @Resource
    private LearningDetailsMapper learningDetailsMapper;
    @Resource
    private TeacherMapper teacherMapper;
    @Resource
    private SourcePowerRankOpt sourcePowerRankOpt;
    @Resource
    private GauntletMapper gauntletMapper;
    @Resource
    private SchoolGoldFactoryMapper schoolGoldFactoryMapper;
    @Resource
    private StudentMapper studentMapper;

    /**
     * 每天 0：02 分执行
     */
    @Scheduled(cron = "0 2 0 * * ?")
    public void initStudyCalendar() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 初始化学习日历开始....");
        this.initStudentDailyLearning();
        this.initLearningDetails();
        this.initPunchRecord();
        this.rankingAward();
        log.info("定时任务 -> 初始化学习日历结束！");
    }

    @Override
    public void initStudentDailyLearning() {
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        //获取昨日登入的学生
        getStudentDaily(beforeDaysDate);
    }

    private void getStudentDaily(Date beforeDaysDate) {
        log.info("定时任务 -> 统计学生详情开始。");
        List<Long> studentIds = runLogMapper.selectLoginStudentId(beforeDaysDate);
        if (studentIds != null && studentIds.size() > 0) {
            //获取学生今日学习时常
            Map<String, Map<String, Object>> studentLoginMap = durationMapper.selectValidTimeByStudentIds(studentIds, beforeDaysDate);
            //是否打卡
            Map<Long, Map<String, Object>> longMapMap = clockInMapper.selectByStudentIds(studentIds, beforeDaysDate);
            //点赞数量
            Map<Long, Map<String, Object>> longMapMap1 = worshipMapper.selectByStudentIdsAndDate(studentIds, beforeDaysDate);
            studentIds.forEach(studentId -> {
                StudentDailyLearning studentDailyLearning = new StudentDailyLearning();
                studentDailyLearning.setStudentId(studentId);
                //创建时间
                studentDailyLearning.setCreateTime(LocalDateTime.now());
                //获取学生登入时间
                Date date = runLogMapper.selectLoginTimeByStudentIdAndDate(studentId, beforeDaysDate);
                //获取学习时间
                if (date != null) {
                    studentDailyLearning.setStudyTime(DateUtil.getLocalDateTime(date));
                    //获取金币数据
                    Integer goldAdd = goldLogMapper.selectGoldByStudentIdAndDate(studentId, date, 1);
                    Integer consumption = goldLogMapper.selectGoldByStudentIdAndDate(studentId, date, 2);
                    studentDailyLearning.setGoldAdd(goldAdd == null ? 0 : goldAdd);
                    studentDailyLearning.setGoldConsumption(consumption == null ? 0 : consumption);
                    //获取学习有效时长
                    Map<String, Object> validTime = studentLoginMap.get(studentId);
                    studentDailyLearning.setValidTime(validTime != null ?
                            Integer.parseInt(validTime.get("validTime").toString()) : 0);
                    Map<String, Object> map = longMapMap.get(studentId);
                    studentDailyLearning.setClockIn(map != null && map.size() > 0 ? 1 : 2);
                    Map<String, Object> map1 = longMapMap1.get(studentId);
                    if (map1 != null) {
                        Object count = map1.get("count");
                        studentDailyLearning.setOiling(count != null ? Integer.parseInt(count.toString()) : 0);
                    } else {
                        studentDailyLearning.setOiling(0);
                    }
                    studentDailyLearningMapper.insert(studentDailyLearning);
                }

            });
        }
        log.info("定时任务 -> 统计学生详情结束。");
    }

    @Override
    public void initLearningDetails() {
        log.info("定时任务 -> 统计每日新增学习详情页数据开始...");
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        // 正在学习的课程
        List<LearnNew> learnNews = learnNewMapper.selectByUpdateTime(beforeDaysDate);
        // 已经学习的课程
        List<LearnHistory> learnHistories = learnHistoryMapper.selectByUpdateTime(beforeDaysDate);

        // 合并相同 类型+课程+单元+group 数据
        Map<String, LearningDetails> map = new HashMap<>(16);
        StringBuilder sb = new StringBuilder();
        learnNews.forEach(learnNew -> this.packageMap(beforeDaysDate, map, sb, learnNew));

        learnHistories.forEach(learnHistory -> this.packageMap(beforeDaysDate, map, sb, learnHistory));

        // 从时长表中获取 类型+课程+单元+group 数据
        Date now = new Date();
        map.forEach((key, value) -> {
            List<Map<String, Object>> durationList = durationMapper.selectByLearningDetails(value, beforeDaysDate);
            if (CollectionUtils.isNotEmpty(durationList)) {
                for (Map<String, Object> stringObjectMap : durationList) {
                    Object validTime = stringObjectMap.get("validTime");
                    Object onlineTime = stringObjectMap.get("onlineTime");
                    Object learningModel = stringObjectMap.get("learningModel");
                    value.setValidTime(validTime == null ? 0 : Long.parseLong(validTime.toString()));
                    value.setOnlineTime(onlineTime == null ? 0 : Long.parseLong(onlineTime.toString()));
                    value.setLearningModel(Integer.parseInt(learningModel.toString()));

                    CourseNew courseNew = courseNewMapper.selectById(value.getCourseId());
                    UnitNew unitNew = unitNewMapper.selectById(value.getUnitId());

                    value.setCourseName(courseNew.getCourseName());
                    value.setUnitName(unitNew.getUnitName());
                    value.setCreateTime(now);
                    learningDetailsMapper.insert(value);
                }
            }
        });

        log.info("定时任务 -> 统计每日新增学习详情页数据结束");
    }

    public void packageMap(Date beforeDaysDate, Map<String, LearningDetails> map, StringBuilder sb, Object object) {
        sb.setLength(0);
        Long courseId;
        Long unitId;
        Integer group;
        Long studentId;
        Integer learnType;

        LearningDetails learningDetails = new LearningDetails();
        if (object instanceof LearnNew) {
            LearnNew learnNew = (LearnNew) object;
            courseId = learnNew.getCourseId();
            unitId = learnNew.getUnitId();
            group = learnNew.getGroup();
            studentId = learnNew.getStudentId();
            learnType = this.getLearnNewType(learnNew.getModelType());
        } else {
            LearnHistory learnHistory = (LearnHistory) object;
            courseId = learnHistory.getCourseId();
            unitId = learnHistory.getUnitId();
            group = learnHistory.getGroup();
            studentId = learnHistory.getStudentId();
            learnType = this.getLearnHistoryType(learnHistory.getType());
        }

        learningDetails.setCourseId(courseId);
        learningDetails.setUnitId(unitId);
        learningDetails.setGroup(group);
        learningDetails.setStudyTime(beforeDaysDate);
        learningDetails.setStudentId(studentId);
        learningDetails.setType(learnType);

        sb.append(learnType).append(courseId).append(unitId).append(group);
        map.put(sb.toString(), learningDetails);
    }

    private Integer getLearnHistoryType(Integer type) {
        switch (type) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 5;
            case 4:
                return 3;
            default:
                return null;
        }
    }

    private Integer getLearnNewType(Integer modelType) {
        switch (modelType) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 5;
            default:
                return null;
        }
    }

    @Override
    public void initPunchRecord() {
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        getPushRecord(beforeDaysDate);
    }

    @Override
    public void rankingAward() {
        //获取当前时间
        Date date = new Date();
        Date startDate = null;
        Date endDate = null;
        String isNowDate = DateUtil.formatYYYYMMDD(date);
        String startDateStr;
        String endDateStr;
        //获取当月日期15号
        Date theSpecifiedDate = DateUtil.getTheSpecifiedDate(date, 15);
        String theSpacifiedDateStr = DateUtil.formatYYYYMMDD(theSpecifiedDate);
        if (theSpacifiedDateStr.equals(isNowDate)) {
            startDate = DateUtil.minTime(DateUtil.getTheSpecifiedDate(date, 1));
            endDate = DateUtil.maxTime(theSpecifiedDate);
        } else {
            //获取当月最后日期
            Date lastDayToMonth = DateUtil.getLastDayToMonth(date);
            theSpacifiedDateStr = DateUtil.formatYYYYMMDD(lastDayToMonth);
            if (theSpacifiedDateStr.equals(isNowDate)) {
                startDate = DateUtil.minTime(theSpecifiedDate);
                endDate = DateUtil.maxTime(lastDayToMonth);
            }
        }
        //获取当天时间是否为奖励发放日期
        if (startDate != null) {
            //获取校区学生排行
            //获取校管id
            List<Long> adminids = teacherMapper.selectAllAdminId();
            startDateStr = DateUtil.formatYYYYMMDDHHMMSS(startDate);
            endDateStr = DateUtil.formatYYYYMMDDHHMMSS(endDate);
            for (Long adminid : adminids) {
                String key = SourcePowerKeysConst.SCHOOL_RANK + adminid;
                long memberSize = sourcePowerRankOpt.getMemberSize(key);
                List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, 0L, memberSize, null);
                //获取校区学生是否在当前区间段pk
                List<Long> getStudentIds = getHaveStudentId(startDateStr, endDateStr, studentIds);
                //获取校区奖励金币
                if (getStudentIds.size() > 0) {
                    getSchoolGold(adminid, getStudentIds);
                }
            }
            String key = SourcePowerKeysConst.COUNTRY_RANK;
            List<Long> studentIds = sourcePowerRankOpt.getReverseRangeMembersBetweenStartAndEnd(key, 0L, 3L, null);
            if (studentIds.size() > 0) {
                for (int i = 0; i < studentIds.size(); i++) {
                    Long aLong = studentIds.get(i);
                    if (i == 0) {
                        getStudentGold(aLong, 1000.0, allMsg);
                    }
                    if (i == 1) {
                        getStudentGold(aLong, 800.0, allMsg);
                    }
                    if (i == 2) {
                        getStudentGold(aLong, 500.0, allMsg);
                    }
                }
            }

        }

    }

    private void getSchoolGold(Long adminid, List<Long> getStudentIds) {
        //获取校区金币
        SchoolGoldFactory schoolGoldFactory = schoolGoldFactoryMapper.selectByAdminId(adminid);
        if (schoolGoldFactory != null && schoolGoldFactory.getGold() != null && schoolGoldFactory.getGold() > 0) {
            Double gold = schoolGoldFactory.getGold();
            Double firstGold = schoolGoldFactory.getGold() * 0.2;
            Double secondGold = schoolGoldFactory.getGold() * 0.15;
            Double thirdGold = schoolGoldFactory.getGold() * 0.10;
            Double fourthGold = schoolGoldFactory.getGold() * 0.08;
            Double fifthGold = schoolGoldFactory.getGold() * 0.05;
            Double supGold = schoolGoldFactory.getGold() - firstGold - secondGold - thirdGold - fourthGold - fifthGold;
            for (int i = 0; i < getStudentIds.size(); i++) {
                Long aLong = getStudentIds.get(i);
                if (i == 0) {
                    getStudentGold(aLong, firstGold, schoolMsg);
                    gold -= firstGold;
                }
                if (i == 1) {
                    getStudentGold(aLong, secondGold, schoolMsg);
                    gold -= secondGold;
                }
                if (i == 2) {
                    getStudentGold(aLong, thirdGold, schoolMsg);
                    gold -= thirdGold;
                }
                if (i == 3) {
                    getStudentGold(aLong, fourthGold, schoolMsg);
                    gold -= fourthGold;
                }
                if (i == 4) {
                    getStudentGold(aLong, fifthGold, schoolMsg);
                    gold -= fifthGold;
                }
                if (i > 4) {
                    getStudentGold(aLong, supGold / getStudentIds.size() - 5, schoolMsg);
                    gold = 0.0;
                }
            }
            schoolGoldFactory.setGold(gold);
            schoolGoldFactoryMapper.updateById(schoolGoldFactory);
        }
    }

    private void getStudentGold(Long studentId, Double gold, String msg) {
        Student student = studentMapper.selectById(studentId);
        student.setSystemGold(student.getSystemGold() + gold);
        GoldLog goldLog = new GoldLog();
        goldLog.setCreateTime(new Date());
        goldLog.setType(1);
        goldLog.setGoldAdd(gold.intValue());
        goldLog.setOperatorId(studentId.intValue());
        goldLog.setStudentId(studentId);
        goldLog.setReadFlag(0);
        goldLog.setReason(msg);
        goldLogMapper.insert(goldLog);
    }

    private List<Long> getHaveStudentId(String startDateStr, String endDateStr, List<Long> studentIds) {
        List<Map<String, Object>> longMapMap =
                gauntletMapper.countByStudentIdsAndStartDateAndEndDate(studentIds, startDateStr, endDateStr);
        List<Map<String, Object>> collect = longMapMap.stream().filter(map ->
                map.get("count") != null && Integer.parseInt(map.get("count").toString()) > 0)
                .collect(Collectors.toList());
        List<Long> haveStudentIds = new ArrayList<>();
        List<Long> getStudentIds = new ArrayList<>();
        collect.forEach(map -> {
            haveStudentIds.add(Long.parseLong(map.get("studentId").toString()));
        });
        studentIds.forEach(studentId -> {
            for (Long haveStudentId : haveStudentIds) {
                if (studentId.equals(haveStudentId)) {
                    getStudentIds.add(studentId);
                    return;
                }
            }
        });
        return getStudentIds;
    }

    private void getPushRecord(Date beforeDaysDate) {
        log.info("定时任务 -> 统计学生点赞数量开始。");
        //统计签到学生
        Map<Long, Map<String, Object>> studentMap = testRecordMapper.selectByGenreAndDate(GenreConstant.SMALLAPP_GENRE, beforeDaysDate);
        Set<Long> longs = studentMap.keySet();
        if (longs.size() > 0) {
            List<Long> list = new ArrayList<>(longs);
            Map<Long, Map<String, Object>> longMapMap1 = worshipMapper.selectByStudentIdsAndDate(list, beforeDaysDate);
            list.forEach(studentId -> {
                PunchRecord punchRecord = new PunchRecord();
                punchRecord.setStudentId(studentId);
                Map<String, Object> map = studentMap.get(studentId);
                punchRecord.setCardTime((LocalDateTime) map.get("date"));
                Map<String, Object> map1 = longMapMap1.get(studentId);
                if (map1 != null) {
                    Object count = map1.get("count");
                    punchRecord.setOiling(count != null ? Integer.parseInt(count.toString()) : 0);
                } else {
                    punchRecord.setOiling(0);
                }
                punchRecord.setCreatTime(LocalDateTime.now());
                punchRecord.setPoint(map.get("count") != null ? Integer.parseInt(map.get("count").toString()) : 0);

                punchRecordMapper.insert(punchRecord);
            });
        }
        log.info("定时任务 -> 统计学生点赞数量结束。");
    }
}
