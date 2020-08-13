package com.zhidejioayu.center;

import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

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

    @Pointcut(value = "execution(* com.zhidejioayu.center.business..*Controller.*(..))")
    public void cut() {
    }

    @Around("cut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object proceed = pjp.proceed();
        printLog(startTime, proceed);
        return proceed;
    }

    private void printLog(long startTime, Object proceed) {
        try {
            long maxTime = 2000;
            HttpSession httpSession = HttpUtil.getHttpSession();
            HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
            if (httpSession == null) {
                log.warn("request 或者 session 为空！不影响程序运行！");
                return;
            }
            String url = httpServletRequest.getRequestURI().substring(httpServletRequest.getContextPath().length());
            this.printSmallAppLog(startTime, maxTime, url);
            if (proceed != null) {
                log.info(" 响应数据 访问接口：[{}], param=[{}], result=[{}]", url, HttpUtil.getParams(), proceed.toString());
            }
        } catch (Exception e) {
            log.error("记录 log 出错！ errMsg=[{}]", e.getMessage());
        }
    }

    /**
     * 打印小程序请求日志
     *
     * @param startTime
     * @param maxTime
     * @param url
     */
    private void printSmallAppLog(long startTime, long maxTime, String url) {
        long time = System.currentTimeMillis() - startTime;
        if (time > maxTime) {
            log.warn("访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                    url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
        } else {
            log.info("访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                    url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
        }
    }
}
