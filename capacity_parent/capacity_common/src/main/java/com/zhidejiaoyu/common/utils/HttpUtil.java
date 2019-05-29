package com.zhidejiaoyu.common.utils;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2019-05-16
 */
public class HttpUtil {

    public static HttpServletRequest getHttpServletRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static HttpSession getHttpSession() {
        return HttpUtil.getHttpServletRequest().getSession();
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    public static String getParams() {
        Map<String, String[]> parameterMap = getHttpServletRequest().getParameterMap();
        StringBuilder sb = new StringBuilder();
        if (parameterMap != null && parameterMap.size() > 0) {
            parameterMap.forEach((key, value) -> sb.append(key).append(":").append(Arrays.toString(value)).append(";"));
        }
        return sb.toString();
    }

    private HttpUtil() {
    }
}
