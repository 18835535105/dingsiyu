package com.zhidejiaoyu.app;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import com.zhidejiaoyu.student.service.impl.BaseServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 异常处理
 *
 * @author 1126
 */
@Slf4j
@ControllerAdvice
public class CatchException extends BaseServiceImpl<StudentMapper, Student> {

    /**
     * 拦截未知异常
     *
     * @param e
     * @param request
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServerResponse unknownException(RuntimeException e, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String param = super.getParameters();
        Object studentObject = session.getAttribute(UserConstant.CURRENT_STUDENT);
        String url = request.getRequestURI().substring(request.getContextPath().length());
        if (studentObject != null) {
            Student student = (Student) studentObject;
            log.error("学生[{}]->[{}],请求 URL=[{}],操作出现系统异常,param=[{}]", student.getId(), student.getStudentName(), url, param, e);
        } else {
            log.error("学生操作出现系统异常,param=[{}], URL=[{}]", param, url, e);
        }
        return ServerResponse.createByError();
    }
}
