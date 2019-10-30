package com.zhidejiaoyu.common.utils;

import org.springframework.web.context.request.RequestAttributes;
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
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return null;
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }

    public static HttpSession getHttpSession() {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
        if (httpServletRequest == null) {
            throw new RuntimeException("HttpServletRequest=null");
        }
        return httpServletRequest.getSession();
    }

    /**
     * 获取请求参数
     *
     * @return
     */
    public static String getParams() {
        HttpServletRequest httpServletRequest = getHttpServletRequest();
        if (httpServletRequest == null) {
            return "";
        }
        Map<String, String[]> parameterMap = httpServletRequest.getParameterMap();
        StringBuilder sb = new StringBuilder();
        if (parameterMap != null && parameterMap.size() > 0) {
            parameterMap.forEach((key, value) -> sb.append(key).append(":").append(Arrays.toString(value)).append(";"));
        }
        return sb.toString();
    }

    private HttpUtil() {
    }
}
