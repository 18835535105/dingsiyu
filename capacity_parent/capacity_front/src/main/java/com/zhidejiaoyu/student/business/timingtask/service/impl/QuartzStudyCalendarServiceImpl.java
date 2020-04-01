package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.*;
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

/**
 * @author: wuchenxi
 * @date: 2020/3/30 16:03:03
 */
@Slf4j
@Service
public class QuartzStudyCalendarServiceImpl implements QuartzStudyCalendarService {

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
            Map<Long, Map<String, Object>> longMapMap = clockInMapper.selectByStudentIds(studentIds, beforeDaysDate);
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
                    //获取学习有效时常
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
