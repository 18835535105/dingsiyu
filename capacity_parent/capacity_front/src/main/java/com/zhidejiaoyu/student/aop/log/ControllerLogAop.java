package com.zhidejiaoyu.student.aop.log;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.student.controller.BaseController;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

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

    @Pointcut(value = "@annotation(com.zhidejiaoyu.student.aop.log.ControllerLogAnnotation)")
    public void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = pjp.proceed();
        printLog(pjp, startTime);
        return proceed;
    }

    private void printLog(ProceedingJoinPoint pjp, long startTime) {
        try {
            Signature signature = pjp.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            ControllerLogAnnotation annotation = method.getAnnotation(ControllerLogAnnotation.class);
            if (annotation != null) {
                Object object = HttpUtil.getHttpSession().getAttribute(UserConstant.CURRENT_STUDENT);
                if (object != null) {
                    String url = HttpUtil.getHttpServletRequest().getRequestURI().substring(HttpUtil.getHttpServletRequest().getContextPath().length());
                    Student student = (Student) object;
                    long time = System.currentTimeMillis() - startTime;
                    log.info("学生[{} -> {} -> {}] 访问接口：[{} -> {}], 用时：[{}] ms, param=[{}]", student.getId(), student.getAccount(), student.getStudentName(), annotation.name(), url, time < 1000 ? time : time / 1000, BaseController.getParams(HttpUtil.getHttpServletRequest()));
                }
            }
        } catch (Exception e) {
            log.error("记录 log 出错！ errMsg=[{}]", e.getMessage());
        }
    }
}
