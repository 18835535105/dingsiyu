package com.zhidejiaoyu.common.aop;

import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;

/**
 * 金币变化时相关操作
 *
 * @author wuchenxi
 * @date 2019-03-28
 */
@Slf4j
@Aspect
@Component
public class GoldChangeAop {

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Autowired
    private TeacherMapper teacherMapper;

    private Double systemGold = null;

    @Pointcut("execution(public * com.zhidejiaoyu.student.service.*.*(..)))")
    public void goldPoint() {
    }

    @Before("goldPoint()")
    public void beforeChange() {
        HttpSession session = request.getSession();
        Object object = session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (object != null) {
            Student student = (Student) object;
            student = studentMapper.selectById(student.getId());
            this.systemGold = student.getSystemGold();
        }
    }

    @After("goldPoint()")
    public void afterChange() {
        HttpSession session = request.getSession();
        Object object = session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (object != null) {
            Student student = (Student) object;
            student = studentMapper.selectById(student.getId());
            if (systemGold != null && !Objects.equals(this.systemGold, student.getSystemGold())) {
                // 金币有变化
                //今日全校排行榜上升10名以上
                dailyAwardAsync.todayUpRank(student);
                // 天道酬勤勋章计算
                medalAwardAsync.upLevel(student, this.getSchoolAdminId(student));
                // 女神勋章（男神勋章）
                medalAwardAsync.godMan(student);
            }
        }
    }

    private Integer getSchoolAdminId(Student student) {
        return student.getTeacherId() == null ? null : teacherMapper.selectSchoolAdminIdByTeacherId(student.getTeacherId());
    }
}
