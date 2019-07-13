package com.zhidejiaoyu.student.common.aop;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 打印有 ControllerLogAnnotation 注解的访问日志
 *
 * @author wuchenxi
 * @date 2019-05-16
 */
@Slf4j
@Aspect
@Component
public class ControllerLogAop {

    @Pointcut(value = "execution(* com.zhidejiaoyu.student.controller..*(..))")
    public void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = pjp.proceed();
        printLog(startTime);
        return proceed;
    }

    private void printLog(long startTime) {
        try {
            long maxTime = 2000;
            Object object = HttpUtil.getHttpSession().getAttribute(UserConstant.CURRENT_STUDENT);
            if (object != null) {
                String url = HttpUtil.getHttpServletRequest().getRequestURI().substring(HttpUtil.getHttpServletRequest().getContextPath().length());
                Student student = (Student) object;
                long time = System.currentTimeMillis() - startTime;
                if (time > maxTime) {
                    log.warn("学生[{} -> {} -> {}] 访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                            student.getId(), student.getAccount(), student.getStudentName(),
                            url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
                } else {
                    log.info("学生[{} -> {} -> {}] 访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                            student.getId(), student.getAccount(), student.getStudentName(),
                            url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
                }
            }
        } catch (Exception e) {
            log.error("记录 log 出错！ errMsg=[{}]", e.getMessage());
        }
    }
}
