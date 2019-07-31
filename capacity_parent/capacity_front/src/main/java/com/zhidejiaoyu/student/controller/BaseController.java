package com.zhidejiaoyu.student.controller;

import com.zhidejiaoyu.common.constant.TimeConstant;
import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.student.service.StudentInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wuchenxi
 * @date 2019-01-30
 */
@RestController
public class BaseController {

    @Autowired
    private StudentInfoService studentInfoService;

    public Student getStudent(HttpSession session) {
        return studentInfoService.selectById(this.getStudentId(session));
    }

    Long getStudentId(HttpSession session) {
        Student student = (Student) session.getAttribute(UserConstant.CURRENT_STUDENT);
        return student.getId();
    }

    /**
     * get请求清学版接口封装通用参数
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

    /**
     * post请求清学版接口封装通用参数
     *
     * @param session
     * @return
     */
    HttpHeaders packageHeader(HttpSession session) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("session", session.toString());
        headers.add("studentId", ((Student) session.getAttribute(UserConstant.CURRENT_STUDENT)).getId().toString());
        headers.add("loginTime", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(session.getAttribute(TimeConstant.LOGIN_TIME)));
        headers.setContentType(MediaType.parseMediaType("application/json; charset=UTF-8"));
        return headers;
    }
}
