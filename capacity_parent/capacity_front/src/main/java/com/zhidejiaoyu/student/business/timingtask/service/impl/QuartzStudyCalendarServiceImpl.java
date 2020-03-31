package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zhidejiaoyu.common.constant.test.GenreConstant;
import com.zhidejiaoyu.common.mapper.*;
import com.zhidejiaoyu.common.pojo.GoldLog;
import com.zhidejiaoyu.common.pojo.PunchRecord;
import com.zhidejiaoyu.common.pojo.RunLog;
import com.zhidejiaoyu.common.pojo.StudentDailyLearning;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzStudyCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    /**
     * 每天 0：02 分执行
     */
    @Scheduled(cron = "0 2 0 * * ?")
    public void initStudyCalendar() {
        if (checkPort(port)) {
            return;
        }
        log.info("初始化学习日历开始....");
        this.initStudentDailyLearning();
        this.initLearningDetails();
        this.initPunchRecord();
        log.info("初始化学习日历结束！");
    }

    @Override
    public void initStudentDailyLearning() {
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        //获取昨日登入的学生
        getStudentDily(beforeDaysDate);
    }

    private void getStudentDily(Date beforeDaysDate) {
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
                Date date = durationMapper.selectLoginTimeByStudentIdAndDate(studentId, beforeDaysDate);
                //获取学习时间
                studentDailyLearning.setStudyTime(DateUtil.getLocalDateTime(date));
                //获取金币数据
                studentDailyLearning.setGoldAdd(goldLogMapper.selectGoldByStudentIdAndDate(studentId, date, 1));
                studentDailyLearning.setGoldConsumption(goldLogMapper.selectGoldByStudentIdAndDate(studentId, date, 2));
                //获取学习有效时常
                studentDailyLearning.setValidTime(
                        Integer.parseInt(studentLoginMap.get(studentId).get("validTime").toString()));
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
            });
        }
        log.info("定时任务 -> 统计学生详情结束。");
    }

    @Override
    public void initLearningDetails() {

    }

    @Override
    public void initPunchRecord() {
        Date beforeDaysDate = DateUtil.getBeforeDaysDate(new Date(), 1);
        getPushRecord(beforeDaysDate);
    }

    @Override
    public void getStudentDailyLearning() {
        Date date = DateUtil.parseYYYYMMDDHHMMSS("2018-10-15 10:15:58");
        while (date.getTime() <= System.currentTimeMillis()) {
            getStudentDily(date);
            getPushRecord(date);
            date = DateUtil.getLastDaysDate(date, 1);
        }

    }

    private void getPushRecord(Date beforeDaysDate) {
        log.info("定时任务 -> 统计学生点赞数量开始。");
        //统计签到学生
        Map<Long, Map<String, Object>> studentMap = testRecordMapper.selectByGenreAndDate(GenreConstant.SMALLAPP_GENRE, beforeDaysDate);
        Set<Long> longs = studentMap.keySet();
        if (longs.size() > 0) {
            List<Long> list = new ArrayList(longs);
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
                punchRecord.setPoint(Integer.parseInt(map.get("count").toString()));
                punchRecordMapper.insert(punchRecord);
            });
        }
        log.info("定时任务 -> 统计学生点赞数量结束。");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void runLogToGoldLog() {
        List<RunLog> runLogs = runLogMapper.selectList(new QueryWrapper<RunLog>().in("type", 4, 5));
        for (RunLog runLog : runLogs) {
            String logContent = runLog.getLogContent();
            if (Objects.equals(runLog.getType(), 4)) {


                int gold = getGold(logContent);

                String reason = "";
                if (logContent.contains("今日首次登陆系统")) {
                    reason = "登录系统";
                } else if (logContent.contains("首次完善资料")) {
                    reason = "首次完善资料";
                } else if (logContent.contains("单元前测")) {
                    reason = "单元前测";
                } else if (logContent.contains("新学大于等于20个单词")) {
                    reason = "新学大于等于20个单词";
                } else if (logContent.contains("新学熟词大于等于10个单词")) {
                    reason = "新学熟词大于等于10个单词";
                } else if (logContent.contains("日奖励")) {
                    reason = "日奖励";
                } else if (logContent.contains("任务奖励")) {
                    reason = "任务奖励";
                } else if (logContent.contains("新学大于等于40个单词")) {
                    reason = "新学大于等于40个单词";
                } else if (logContent.contains("新学熟词大于等于20个单词")) {
                    reason = "新学熟词大于等于20个单词";
                } else if (logContent.contains("抽奖获得")) {
                    reason = "抽奖获得";
                } else if (logContent.contains("抽獎中奖励")) {
                    reason = "抽奖获得";
                } else if (logContent.contains("句型游戏")) {
                    reason = "句型游戏";
                } else if (logContent.contains("桌牌捕音")) {
                    reason = "桌牌捕音";
                } else if (logContent.contains("学前游戏测试")) {
                    reason = "学前游戏测试";
                } else if (logContent.contains("单元闯关测试")) {
                    reason = "单元闯关测试";
                } else if (logContent.contains("在校区副本挑战")) {
                    reason = "在校区副本挑战";
                }

                goldLogMapper.insert(GoldLog.builder()
                        .type(1)
                        .createTime(runLog.getCreateTime())
                        .goldAdd(gold)
                        .goldReduce(0)
                        .reason(reason)
                        .operatorId(Integer.parseInt(runLog.getOperateUserId().toString()))
                        .readFlag(2)
                        .studentId(runLog.getOperateUserId())
                        .build());

            } else if (Objects.equals(runLog.getType(), 5)) {
                String reason = "";
                if (logContent.contains("单元闯关测试")) {
                    reason = "单元闯关测试";
                } else if (logContent.contains("pk对战")) {
                    reason = "pk对战";
                }

                int gold = getGold(logContent);
                goldLogMapper.insert(GoldLog.builder()
                        .type(1)
                        .createTime(runLog.getCreateTime())
                        .goldAdd(0)
                        .goldReduce(gold)
                        .reason(reason)
                        .operatorId(Integer.parseInt(runLog.getOperateUserId().toString()))
                        .readFlag(2)
                        .studentId(runLog.getOperateUserId())
                        .build());
            }
        }
    }

    public int getGold(String logContent) {
        return Integer.parseInt(logContent.split("#")[1]);
    }

    public static void main(String[] args) {
        System.out.println("抽奖获得金币#5#".split("#")[1]);
    }
}
