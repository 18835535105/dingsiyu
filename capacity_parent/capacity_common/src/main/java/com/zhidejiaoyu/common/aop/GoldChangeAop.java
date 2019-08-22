package com.zhidejiaoyu.common.aop;

import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.mapper.TeacherMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.rank.RankOpt;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.*;
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

    @Autowired
    private RankOpt rankOpt;

    private Double systemGold = null;

    @Pointcut("@annotation(com.zhidejiaoyu.common.annotation.GoldChangeAnnotation)")
    public void goldPoint() {
    }

    @Before("goldPoint()")
    public void beforeChange() {
        try {
            Student student = this.getStudent();
            if (student != null) {
                this.systemGold = student.getSystemGold();
            }
        } catch (Exception e) {
            log.error("[{}] @before操作错误！", this.getClass().getSimpleName(), e);
        }
    }

    @AfterReturning("goldPoint()")
    public void afterChange() {
        Student student = this.getStudent();
        try {
            if (student != null && systemGold != null && !Objects.equals(this.systemGold, student.getSystemGold())) {
                // 金币有变化
                //今日全校排行榜上升10名以上
                dailyAwardAsync.todayUpRank(student);
                // 天道酬勤勋章计算
                medalAwardAsync.upLevel(student, this.getSchoolAdminId(student));
                // 女神勋章（男神勋章）
                medalAwardAsync.godMan(student);
                // 拔得头筹
                medalAwardAsync.theFirst(student);

                rankOpt.optGoldRank(student);
            }
        } catch (Exception e) {
            if (student != null) {
                log.error("学生[{} - {} - {}] 操作错误！", student.getId(), student.getAccount(), student.getStudentName(), e);
            }
        }
    }

    private Integer getSchoolAdminId(Student student) {
        if (student.getTeacherId() == null) {
            return null;
        }
        Integer schoolAdminId = teacherMapper.getSchoolAdminById(Integer.valueOf(student.getTeacherId().toString()));
        if (schoolAdminId == null) {
            return Integer.valueOf(student.getTeacherId().toString());
        }
        return schoolAdminId;
    }

    private Student getStudent() {
        HttpSession session = request.getSession();
        Object object = session.getAttribute(UserConstant.CURRENT_STUDENT);
        if (object != null) {
            Student student = (Student) object;
            return studentMapper.selectById(student.getId());
        }
        return null;
    }
}
