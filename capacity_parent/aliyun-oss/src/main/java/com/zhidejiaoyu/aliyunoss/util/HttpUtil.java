package com.zhidejiaoyu.aliyunoss.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * @author wuchenxi
 * @date 2019-07-12
 */
public class HttpUtil {
    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getHttpSession() {
        return HttpUtil.getHttpServletRequest().getSession();
    }
}
