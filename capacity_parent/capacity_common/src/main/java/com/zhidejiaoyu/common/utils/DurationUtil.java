package com.zhidejiaoyu.common.utils;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.DurationMapper;
import com.zhidejiaoyu.common.pojo.Duration;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.dateUtlis.DateUtil;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.SessionCookieConfig;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Objects;

/**
 * 时长相关工具类
 *
 * @author: wuchenxi
 * @Date: 2019-09-16 10:11
 */
@Component
public class DurationUtil {

    private static DurationMapper durationMapper;

    @Autowired
    private DurationMapper durationMapperTmp;

    @PostConstruct
    public void init() {
        durationMapper = this.durationMapperTmp;
    }

    /**
     * 上次学习结束时间至当前时间的在线时长（单位：秒）
     *
     * @param student
     * @return
     */
    public static long getOnlineTimeBetweenThisAndLast(Student student, Date loginTime) {
        // 本次学习结束距离上次学习结束的在线时长
        Duration duration = durationMapper.countOnlineTimeWithLoginOutTine(student.getId());

        if (duration == null
                || !Objects.equals(DateUtil.formatYYYYMMDDHHMMSS(loginTime), DateUtil.formatYYYYMMDDHHMMSS(duration.getLoginTime()))) {
            return (System.currentTimeMillis() - loginTime.getTime()) / 1000;
        }
        return (System.currentTimeMillis() - duration.getLoginOutTime().getTime()) / 1000;
    }

    /**
     * 获取学生今日的在线时长
     *
     * @param session
     * @return
     */
    public static long getTodayOnlineTime(HttpSession session) {
        Student student  = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        String todayTime = DateUtil.formatYYYYMMDD(new Date());
        // 今日本次登录前的在线时长
        Date loginTime = (Date) session.getAttribute(TimeConstant.LOGIN_TIME);
        Integer preThisLoginOnlineTine = durationMapper.selectOnlineTime(student.getId(), todayTime + " 00:00:00",
                DateUtil.formatYYYYMMDDHHMMSS(loginTime));

        // 今日本次登录的在线时长
        long thisLoginOnlineTime = (System.currentTimeMillis() - loginTime.getTime()) / 1000;

        if (preThisLoginOnlineTine == null) {
            return thisLoginOnlineTime;
        }
        return thisLoginOnlineTime + preThisLoginOnlineTine;
    }
}
