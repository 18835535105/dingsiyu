package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.mapper.ClockInMapper;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.UpLevelConfigMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.pojo.UpLevelConfig;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.smallapp.studyinfo.DailyStateVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzRobotService;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author: wuchenxi
 * @date: 2020/4/7 17:56:56
 */
@Service
public class QuartzRobotServiceImpl extends BaseServiceImpl<StudentMapper, Student> implements QuartzRobotService {

    @Resource
    private DurationMapper durationMapper;

    @Resource
    private ClockInMapper clockInMapper;

    @Resource
    private StudentMapper studentMapper;

    @Resource
    private UpLevelConfigMapper upLevelConfigMapper;

    @Override
    public ServerResponse<Object> getDailyState(String account) {

        // 当天所有学生在线时长平均值
        Double avg = durationMapper.selectAvgOnlineTime();
        int avgOnlineTime = 0;
        if (avg != null) {
            avgOnlineTime = (int) Math.floor(avg) / 60;
        }
        Date date = new Date();

        UpLevelConfig upLevelConfig = upLevelConfigMapper.selectById(1);
        int smallToMiddleDays = this.getUpLevelDays(date, upLevelConfig.getSmallToMiddle());
        int middleToHighDays = this.getUpLevelDays(date, upLevelConfig.getMiddleToHigh());
        int highToBigDays = this.getUpLevelDays(date, upLevelConfig.getHighToBig());

        Map<String, Integer> phaseDaysMap = new HashMap<>(16);
        phaseDaysMap.put("小学", smallToMiddleDays);
        phaseDaysMap.put("初中", middleToHighDays);
        phaseDaysMap.put("高中", highToBigDays);

        String[] accountArr = account.split(",");

        List<DailyStateVO> clockIns = clockInMapper.selectByStudentAccount(accountArr, date);
        List<Long> clockInStudentIds = clockIns.stream().map(DailyStateVO::getStudentId).collect(Collectors.toList());
        List<DailyStateVO> durationInfoVos = durationMapper.selectDailyStateVOLearnDateAndOnlineTime(accountArr, clockInStudentIds);

        // 今日所有学生的在线时长
        List<Long> onlineTimes = clockIns.stream().map(DailyStateVO::getOnlineTime).collect(Collectors.toList());
        onlineTimes.addAll(durationInfoVos.stream().map(DailyStateVO::getOnlineTime).collect(Collectors.toList()));

        // 用于存放在线时长对应的名次
        Map<Long, Integer> sortMap = new HashMap<>(16);
        onlineTimes.sort((a, b) -> (int) (a - b));
        int totalStudent = onlineTimes.size();
        for (int i = 0; i < totalStudent; i++) {
            sortMap.put(onlineTimes.get(i), i + 1);
        }

        // 本周学生的在线时长
        Map<Long, Map<String, Object>> weekOnlineTime = durationMapper.selectWeekOnlineTime(accountArr, DateUtil.getDateOfWeekDay(1), DateUtil.getDateOfWeekDay(7));

        String dateStr = DateUtil.formatDate(date, "yyyy年MM月dd日");

        // 当天有学习记录或者打卡记录的学生id
        Map<Long, Long> studentIdsMap = new HashMap<>(16);

        StringBuilder sb = new StringBuilder();
        int finalAvgOnlineTime = avgOnlineTime;

        // 未打卡的学生
        List<DailyStateVO> collect = durationInfoVos.stream().peek(dailyStateVO -> {
            // 查询指定学习日期学习的模块
            Long studentId = dailyStateVO.getStudentId();
            studentIdsMap.put(studentId, studentId);

            sb.setLength(0);

            int progress = this.getWeekProgress(weekOnlineTime, studentId);
            String phase = dailyStateVO.getPhase();
            sb.append(dailyStateVO.getStudentName()).append("本周学习进度").append(progress).append("%，")
                    .append(dateStr).append("未在小程序上进行复习，请您督促，")
                    .append("PC学习时长：").append(dailyStateVO.getOnlineTime() / 60).append("分钟，")
                    .append("离").append(this.getPhaseMsg(phase))
                    .append("还有大约").append(phaseDaysMap.get(phase)).append("天");

            dailyStateVO.setMsg(sb.toString());
        }).collect(Collectors.toList());

        // 已打卡的学生
        collect.addAll(clockIns.stream().peek(clockIn -> {
            Long studentId = clockIn.getStudentId();
            studentIdsMap.put(studentId, studentId);

            sb.setLength(0);
            String phase = clockIn.getPhase();
            int progress = this.getWeekProgress(weekOnlineTime, studentId);
            sb.append(clockIn.getStudentName()).append("本周学习进度").append(progress).append("%，")
                    .append(dateStr).append("在小程序上连续复习").append(clockIn.getCardDays()).append("天，")
                    .append("PC学习时长：").append(clockIn.getOnlineTime() / 60).append("分钟，")
                    .append("离").append(this.getPhaseMsg(phase))
                    .append("还有大约").append(phaseDaysMap.get(phase)).append("天");

            clockIn.setMsg(sb.toString());

            // 在线时长超过的百分比
            int percent = this.getPercent(sortMap, totalStudent, clockIn);
            sb.setLength(0);
            sb.append("夺分队长").append(dateStr).append("全国平均学习时长：").append(finalAvgOnlineTime)
                    .append("分钟，").append(clockIn.getStudentName()).append("的学习时长超过")
                    .append(percent).append("%的孩子");
            clockIn.setMsg1(sb.toString());

        }).collect(Collectors.toList()));

        // 既没有打卡也没有学习的学生
        List<DailyStateVO> studentInfos = studentMapper.selectByAccounts(accountArr);
        collect.addAll(studentInfos.stream()
                // 过滤出当天既没有学习记录也没有打卡记录的学生id
                .filter(dailyStateVO -> !studentIdsMap.containsKey(dailyStateVO.getStudentId()))
                .peek(dailyStateVO -> {
                    Long studentId = dailyStateVO.getStudentId();
                    dailyStateVO.setAccount(dailyStateVO.getAccount());
                    sb.setLength(0);
                    int progress = this.getWeekProgress(weekOnlineTime, studentId);
                    String phase = dailyStateVO.getPhase();
                    sb.append(dailyStateVO.getStudentName()).append("本周学习进度").append(progress).append("%，")
                            .append(dateStr).append("未再小程序上进行复习，请您督促，")
                            .append("PC学习时长：0分钟，")
                            .append("离").append(this.getPhaseMsg(phase))
                            .append("还有大约").append(phaseDaysMap.get(phase)).append("天");

                    dailyStateVO.setMsg(sb.toString());
                }).collect(Collectors.toList()));

        return ServerResponse.createBySuccess(collect);
    }

    public int getPercent(Map<Long, Integer> sortMap, int totalStudent, DailyStateVO dailyStateVO) {
        Integer rank = sortMap.get(dailyStateVO.getOnlineTime());
        if (rank == null || rank == 1) {
            return 0;
        }

        return (int) ((rank * 1.0 / totalStudent) * 100);
    }


    /**
     * 获取升级提示语
     *
     * @param phase
     * @return
     */
    private String getPhaseMsg(String phase) {
        switch (phase) {
            case "小学":
                return "小升初";
            case "初中":
                return "初升高";
            case "高中":
                return "高考";
            default:
                return "";
        }
    }

    /**
     * 获取升级还需要多少天
     *
     * @param date
     * @param upLevelDateStr
     * @return
     */
    public int getUpLevelDays(Date date, String upLevelDateStr) {
        String yyyy = DateUtil.formatDate(date, "yyyy");
        Date smallToMiddleDate = new DateTime(Integer.parseInt(yyyy), Integer.parseInt(upLevelDateStr.split("-")[0]), Integer.parseInt(upLevelDateStr.split("-")[1]), 0, 0).toDate();
        if (smallToMiddleDate.getTime() <= date.getTime()) {
            smallToMiddleDate = new DateTime(Integer.parseInt(yyyy) + 1, Integer.parseInt(upLevelDateStr.split("-")[0]), Integer.parseInt(upLevelDateStr.split("-")[1]), 0, 0).toDate();
        }
        return (int) ((smallToMiddleDate.getTime() - date.getTime()) / 1000 / 60 / 60 / 24);
    }

    /**
     * 获取本周学习进度
     *
     * @param weekOnlineTime
     * @param studentId
     * @return
     */
    public int getWeekProgress(Map<Long, Map<String, Object>> weekOnlineTime, Long studentId) {
        String onlineTimeStr = "onlineTime";
        if (weekOnlineTime.get(studentId) != null && weekOnlineTime.get(studentId).get(onlineTimeStr) != null) {
            // 本周学习的小时数
            long m = Long.parseLong(weekOnlineTime.get(studentId).get(onlineTimeStr).toString()) / 3600;
            return m > 4 ? 100 : (int) (m * 1.0 / 4 * 100);
        } else {
            return 0;
        }
    }

    public static void main(String[] args) {
        System.out.println(DateUtil.formatYYYYMMDDHHMMSS(new DateTime().withDayOfWeek(1).toDate()));
        System.out.println(DateUtil.formatYYYYMMDDHHMMSS(new DateTime().withDayOfWeek(7).toDate()));
    }
}
