package com.zhidejiaoyu.common.aop;

import com.zhidejiaoyu.common.annotation.TestChangeAnnotation;
import com.zhidejiaoyu.common.award.DailyAwardAsync;
import com.zhidejiaoyu.common.award.GoldAwardAsync;
import com.zhidejiaoyu.common.award.MedalAwardAsync;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 单元闯关测试变化时相关操作
 *
 * @author wuchenxi
 * @date 2019-03-28
 */
@Slf4j
@Aspect
@Component
public class UnitTestChangeAop {

    @Autowired
    private StudentMapper studentMapper;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private GoldAwardAsync goldAwardAsync;

    @Autowired
    private DailyAwardAsync dailyAwardAsync;

    @Autowired
    private MedalAwardAsync medalAwardAsync;

    @Pointcut("@annotation(com.zhidejiaoyu.common.annotation.TestChangeAnnotation)")
    public void testPoint() {}

    @Around("testPoint()")
    public Object optTest(ProceedingJoinPoint pjp) throws Throwable {
        Object proceed = pjp.proceed();
        try {
            this.opt(pjp);
        } catch (Exception e) {
            log.error("[{}] @Arount 操作出错", this.getClass().getName(), e);
        }
        return proceed;
    }

    private void opt(ProceedingJoinPoint pjp) {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        if (method != null) {
            TestChangeAnnotation annotation = method.getAnnotation(TestChangeAnnotation.class);
            if (annotation != null) {
                Student student = this.getStudent();
                if (annotation.isUnitTest()) {
                    this.afterUnitTest(student);
                } else {
                    this.afterTest(student);
                }
            }
        }
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

    /**
     * 完成单元闯关测试之后操作
     *
     * @param student
     */
    private void afterUnitTest(Student student) {
        if (student != null) {
            // 验证学生今日是否完成一个单元
            dailyAwardAsync.todayLearnOneUnit(student);
            // 验证学生今日是否完成10个单元闯关测试
            dailyAwardAsync.todayCompleteTenUnitTest(student);
            // 验证学生单元闯关成功个数
            goldAwardAsync.completeUnitTest(student);
        }
    }

    /**
     * 所有测试保存后操作
     *
     * @param student
     */
    private void afterTest(Student student) {
        if (student != null) {
            String point = request.getParameter("point");
           this.afterUnitTest(student);
            // 最有潜力勋章
            medalAwardAsync.potentialMan(student);
            if (Objects.equals(point, "100")) {
                // 学霸崛起勋章计算
                medalAwardAsync.superStudent(student);
            }
        }
    }
}
