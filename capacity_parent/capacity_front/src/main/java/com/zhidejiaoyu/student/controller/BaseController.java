package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.HashMap;
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

    /**
     * 请求清学版接口封装通用参数
     *
     * @param session
     * @return
     */
    Map<String, Object> packageParams(HttpSession session) {
        Map<String, Object> paramMap = new HashMap<>(16);
        paramMap.put("session", session);
        paramMap.put("studentId", ((Student)session.getAttribute(UserConstant.CURRENT_STUDENT)).getId());
        paramMap.put("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        return paramMap;
    }
}
