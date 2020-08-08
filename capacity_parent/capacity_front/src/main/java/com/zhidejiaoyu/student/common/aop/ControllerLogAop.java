package com.zhidejiaoyu.student.common.aop;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
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

    @Pointcut(value = "execution(* com.zhidejiaoyu.student..*Controller.*(..))")
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
            if (StringUtils.isNotEmpty(url) && (url.contains("/smallApp") || url.contains("publicAccount") || url.contains("/qy"))) {
                this.printSmallAppLog(startTime, maxTime, url, proceed);
            } else {
                this.printLearnSystemLog(startTime, maxTime, httpSession, url, proceed);
            }
        } catch (Exception e) {
            log.error("记录 log 出错！ errMsg=[{}]", e.getMessage());
        }
    }

    /**
     * 打印学习系统请求日志
     *  @param startTime
     * @param maxTime
     * @param httpSession
     * @param url
     * @param proceed
     */
    private void printLearnSystemLog(long startTime, long maxTime, HttpSession httpSession, String url, Object proceed) {
        Object object = httpSession.getAttribute(UserConstant.CURRENT_STUDENT);
        if (object != null) {
            Student student = (Student) object;
            long time = System.currentTimeMillis() - startTime;
            // 保存好声音的接口不打印 warn 级别日志
            if (time > maxTime && StringUtils.isNotEmpty(url) && !url.contains("/voice/")) {
                log.warn("学生[{} -> {} -> {}] 访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                        student.getId(), student.getAccount(), student.getStudentName(),
                        url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
            } else {
                log.info("学生[{} -> {} -> {}] 访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                        student.getId(), student.getAccount(), student.getStudentName(),
                        url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
            }
            if (proceed != null) {
                log.info("响应数据 学生[{} -> {} -> {}] 访问接口：[{}], 响应结果：[{}]",
                        student.getId(), student.getAccount(), student.getStudentName(),
                        url, proceed.toString());
            }
        }
    }

    /**
     * 打印小程序请求日志
     *  @param startTime
     * @param maxTime
     * @param url
     * @param proceed
     */
    private void printSmallAppLog(long startTime, long maxTime, String url, Object proceed) {
        long time = System.currentTimeMillis() - startTime;
        if (time > maxTime) {
            log.warn("访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                    url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
        } else {
            log.info("访问接口：[{}], 用时：[{}], thread：[{}] param=[{}]",
                    url, time + " ms", Thread.currentThread().getName(), HttpUtil.getParams());
        }
        if (proceed != null) {
            log.info("响应数据 访问接口：[{}], 响应结果：[{}]",
                    url, proceed.toString());
        }

    }
}
