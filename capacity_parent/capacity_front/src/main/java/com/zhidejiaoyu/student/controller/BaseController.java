package com.zhidejiaoyu.student.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2019-01-30
 */
@RestController
public class BaseController {

    @Autowired
    private HttpServletRequest request;


    public String getParams() {
        return getParams(request);
    }

    public static String getParams(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        StringBuilder sb = new StringBuilder();
        if (parameterMap != null && parameterMap.size() > 0) {
            parameterMap.forEach((key, value) -> sb.append(key).append(":").append(Arrays.toString(value)).append(";"));
        }
        return sb.toString();
    }
}
