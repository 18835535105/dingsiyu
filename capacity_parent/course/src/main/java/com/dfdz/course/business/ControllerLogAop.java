package com.dfdz.course.business;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

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

    @Pointcut(value = "execution(* com.dfdz.course.business.controller..*Controller.*(..))")
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
            HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
            String url = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
            long time = System.currentTimeMillis() - startTime;
            log.info("访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                    url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
        } catch (Exception e) {
            log.error("记录 log 出错！ errMsg=[{}]", e.getMessage());
        }
    }

}
