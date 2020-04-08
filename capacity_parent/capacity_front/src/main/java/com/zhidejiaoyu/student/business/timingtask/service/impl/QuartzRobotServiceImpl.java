package com.zhidejiaoyu.student.business.timingtask.service.impl;

import com.zhidejiaoyu.common.mapper.ClockInMapper;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import com.zhidejiaoyu.common.utils.duration.DurationStudyModelUtil;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.common.vo.smallapp.studyinfo.DailyStateVO;
import com.zhidejiaoyu.student.business.service.impl.BaseServiceImpl;
import com.zhidejiaoyu.student.business.timingtask.service.QuartzRobotService;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
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

    @Override
    public ServerResponse<Object> getDailyState(String account) {

        // 当天所有学生在线时长平均值
        Double avg = durationMapper.selectAvgOnlineTime();
        int avgOnlineTime = 0;
        if (avg != null) {
            avgOnlineTime = (int) Math.floor(avg);
        }

        String[] accountArr = account.split(",");

        List<DailyStateVO> durationInfoVos = durationMapper.selectDailyStateVOLearnDateAndOnlineTime(accountArr);

        Date date = new Date();
        String dateStr = DateUtil.formatDate(date, "yyyy年MM月dd日");

        List<DailyStateVO> clockIns = clockInMapper.selectByStudentAccount(accountArr, date);
        Map<Long, List<DailyStateVO>> clockInMap = clockIns.stream().collect(Collectors.groupingBy(DailyStateVO::getStudentId));

        // 当天有学习记录或者打卡记录的学生id
        Map<Long, Long> studentIdsMap = new HashMap<>(16);

        StringBuilder sb = new StringBuilder();
        Set<String> set = new HashSet<>();

        if (CollectionUtils.isNotEmpty(durationInfoVos)) {
            durationInfoVos.forEach(dailyStateVO -> {
                // 查询指定学习日期学习的模块
                Long studentId = dailyStateVO.getStudentId();
                studentIdsMap.put(studentId, studentId);
                List<DailyStateVO> dailyStateVos = durationMapper.selectTodayDurationInfos(studentId);

                sb.setLength(0);
                set.clear();
                dailyStateVos.forEach(dailyStateVo -> {
                    String studyModelStr = DurationStudyModelUtil.getStudyModelStr(dailyStateVo.getStudyModel());
                    if (StringUtils.isNotEmpty(studyModelStr)) {
                        set.add(studyModelStr);
                    }
                });

                set.forEach(s -> sb.append(s).append(","));

                dailyStateVO.setLearnDate(dateStr);
                dailyStateVO.setOnlineTime(dailyStateVO.getOnlineTime() / 60);
                dailyStateVO.setStudyModelStr(sb.length() > 1 ? sb.toString().substring(0, sb.length() - 1) : "单词");
                dailyStateVO.setCard(clockInMap.containsKey(studentId));
            });
            DailyState dailyState = new DailyState();
            dailyState.setAvgOnlineTime(avgOnlineTime / 60);
            List<DailyStateVO> ignoreDailyStateVo = this.getIgnoreVo(accountArr, studentIdsMap);
            durationInfoVos.addAll(ignoreDailyStateVo);
            dailyState.setVos(durationInfoVos);
            return ServerResponse.createBySuccess(dailyState);
        }

        List<DailyStateVO> collect = clockIns.stream().map(clockIn -> {
            Long studentId = clockIn.getStudentId();
            studentIdsMap.put(studentId, studentId);

            DailyStateVO dailyStateVO = new DailyStateVO();
            dailyStateVO.setStudentId(studentId);
            dailyStateVO.setCard(true);
            dailyStateVO.setAccount(clockIn.getAccount());
            dailyStateVO.setStudentName(clockIn.getStudentName());
            return dailyStateVO;
        }).collect(Collectors.toList());

        List<DailyStateVO> ignoreDailyStateVo = this.getIgnoreVo(accountArr, studentIdsMap);
        collect.addAll(ignoreDailyStateVo);

        DailyState dailyState = new DailyState();
        dailyState.setAvgOnlineTime(avgOnlineTime / 60);
        dailyState.setVos(collect);
        return ServerResponse.createBySuccess(dailyState);
    }

    /**
     * 统计既没有学习记录也没有打卡记录的学生的信息
     *
     * @param accountArr
     * @param studentIdsMap
     * @return
     */
    public List<DailyStateVO> getIgnoreVo(String[] accountArr, Map<Long, Long> studentIdsMap) {
        List<Student> students = studentMapper.selectByAccounts(accountArr);

        return students.stream()
                // 过滤出当天既没有学习记录也没有打卡记录的学生id
                .filter(student -> !studentIdsMap.containsKey(student.getId()))
                .map(student -> {
                    DailyStateVO dailyStateVO = new DailyStateVO();
                    dailyStateVO.setAccount(student.getAccount());
                    dailyStateVO.setStudentName(student.getStudentName());
                    dailyStateVO.setPetName(student.getPetName());
                    return dailyStateVO;
                }).collect(Collectors.toList());
    }

    @Data
    static class DailyState {
        /**
         * 所有学生在线时长平均值
         */
        private Integer avgOnlineTime;

        private List<DailyStateVO> vos;
    }
}
