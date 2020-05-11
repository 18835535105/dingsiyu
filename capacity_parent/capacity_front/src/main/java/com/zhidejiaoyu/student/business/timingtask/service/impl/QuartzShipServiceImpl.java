package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.mapper.PkCopyStateMapper;
import com.zhidejiaoyu.common.mapper.TotalHistoryPlanMapper;
import com.zhidejiaoyu.common.mapper.WeekHistoryPlanMapper;
import com.zhidejiaoyu.common.pojo.TotalHistoryPlan;
import com.zhidejiaoyu.common.pojo.WeekHistoryPlan;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.student.business.shipconfig.constant.EquipmentTypeConstant;
import com.zhidejiaoyu.student.business.shipconfig.service.ShipAddEquipmentService;
import com.zhidejiaoyu.student.business.timingtask.service.BaseQuartzService;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzShipService;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/3/19 09:35:35
 */
@Slf4j
@Service
public class QuartzShipServiceImpl implements QuartzShipService, BaseQuartzService {

    @Value("${quartz.port}")
    private int port;

    @Resource
    private PkCopyStateMapper pkCopyStateMapper;

    @Resource
    private WeekHistoryPlanMapper weekHistoryPlanMapper;

    @Resource
    private TotalHistoryPlanMapper totalHistoryPlanMapper;

    @Resource
    private ShipAddEquipmentService shipAddEquipmentService;

    /**
     * 每天 0：15 分执行
     */
    @Scheduled(cron = "0 15 0 * * ?")
    public void initStudyCalendar() {
        if (checkPort(port)) {
            return;
        }
        log.info("定时任务 -> 初始化修改每日解锁进度开始....");
        this.weekUnclock();
        log.info("定时任务 -> 初始化修改每日解锁进度结束！");
        log.info("定时任务 -> 初始化修改解锁进度开始....");
        this.totalUnclock();
        log.info("定时任务 -> 初始化修改解锁进度结束！");
    }

    @Override
    public void weekUnclock() {
        String times = DateUtil.getBeforeDayDateStr(new Date(), 1, DateUtil.YYYYMMDDHHMMSS);
        List<WeekHistoryPlan> weekHistoryPlans = weekHistoryPlanMapper.selectAllByTime(times);
        Map<Long, List<WeekHistoryPlan>> collect = weekHistoryPlans.stream().collect(Collectors.groupingBy(plan -> plan.getStudentId()));
        Set<Long> longs = collect.keySet();
        longs.forEach(studentId -> {
            List<WeekHistoryPlan> plans = collect.get(studentId);
            if (plans.size() > 0) {
                totalHistoryPlanMapper.selectByStudentId(studentId);
                WeekHistoryPlan weekHistoryPlan = plans.get(0);
                /**
                 * 添加时常
                 */
                weekHistoryPlan.setOnlineTime(
                        shipAddEquipmentService.getTime(studentId, times, weekHistoryPlan, EquipmentTypeConstant.ONLINE_TIME_MAX, 1).longValue());
                /**
                 * 添加单词
                 */
                long wordValue = shipAddEquipmentService.getEmpValue(studentId, 3, times);
                weekHistoryPlan.setWord(
                        shipAddEquipmentService.getWordAnPoint(EquipmentTypeConstant.WORD_MAX, wordValue, weekHistoryPlan.getWord()));

                /**
                 * 添加分数
                 */
                long pointValue = shipAddEquipmentService.getEmpValue(studentId, 3, times);
                weekHistoryPlan.setPoint(
                        shipAddEquipmentService.getWordAnPoint(EquipmentTypeConstant.POINT_MAX, pointValue, weekHistoryPlan.getPoint()));
                /**
                 * 添加有效时常
                 */
                weekHistoryPlan.setValidTime(
                        shipAddEquipmentService.getTime(studentId, times, weekHistoryPlan, EquipmentTypeConstant.VALID_TIME_MAX, 4).longValue());

                weekHistoryPlanMapper.updateById(weekHistoryPlan);
            }
        });
    }

    @Override
    public void totalUnclock() {
        Date weekStart = DateUtil.minTime(DateUtil.getWeekStart());
        Date date = DateUtil.minTime(new Date());
        if (weekStart.getTime() != date.getTime()) {
            return;
        }
        String times = DateUtil.getBeforeDayDateStr(new Date(), 1, DateUtil.YYYYMMDDHHMMSS);
        List<WeekHistoryPlan> weekHistoryPlans = weekHistoryPlanMapper.selectAllByTime(times);
        Map<Long, List<WeekHistoryPlan>> collect = weekHistoryPlans.stream().collect(Collectors.groupingBy(plan -> plan.getStudentId()));
        Set<Long> longs = collect.keySet();
        longs.forEach(studentId -> {
            List<WeekHistoryPlan> plans = collect.get(studentId);
            if (plans.size() > 0) {
                WeekHistoryPlan weekHistoryPlan = plans.get(0);
                TotalHistoryPlan totalHistoryPlan = totalHistoryPlanMapper.selectByStudentId(studentId);
                totalHistoryPlan.setTotalVaildTime(totalHistoryPlan.getTotalOnlineTime() + weekHistoryPlan.getOnlineTime());
                totalHistoryPlan.setTotalOnlineTime(totalHistoryPlan.getTotalVaildTime() + weekHistoryPlan.getValidTime());
                totalHistoryPlan.setTotalPoint(totalHistoryPlan.getTotalPoint() + weekHistoryPlan.getPoint());
                totalHistoryPlan.setTotalWord(totalHistoryPlan.getTotalWord() + weekHistoryPlan.getWord());
                totalHistoryPlanMapper.updateById(totalHistoryPlan);
            }
        });
    }

    @Override
    @Scheduled(cron = "0 2 0 * * ?")
    public void deleteSchoolCopy() {
        if (checkPort(port)) {
            return;
        }

        // 星期几
        int week = new DateTime().dayOfWeek().get();
        if (week != 1) {
            // 不是周一不执行以下删除逻辑
            log.info("当前日期不是星期一！");
            return;
        }

        log.info("定时删除校区副本挑战状态开始。。。");

        pkCopyStateMapper.deleteSchoolCopy();

        log.info("定时删除校区副本挑战状态完成。");
    }

    public static void main(String[] args) {
        System.out.println(new DateTime().dayOfWeek().get());
    }
}
