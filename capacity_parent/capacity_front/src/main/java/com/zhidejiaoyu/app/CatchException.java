package com.zhidejiaoyu.app;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.exception.Enum.ServiceExceptionEnum;
import com.zhidejiaoyu.common.exception.ServiceException;
import com.zhidejiaoyu.common.mapper.StudentMapper;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
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
import javax.validation.ConstraintViolationException;
import java.util.ArrayList;

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
        packageLogMsg(e, request);
        return ServerResponse.createByError(ServiceExceptionEnum.EXCEPTION.getCode(), ServiceExceptionEnum.EXCEPTION.getMessage());
    }

    /**
     * 拦截已知异常
     *
     * @param e
     * @param request
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServerResponse knownException(ServiceException e, HttpServletRequest request) {
        packageLogMsg(e, request);
        return ServerResponse.createByError(e.getCode(), e.getMessage());
    }

    /**
     * 拦截参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServerResponse constraintViolationException(ConstraintViolationException e) {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
        if (httpServletRequest != null) {
            packageLogMsg(e, httpServletRequest);
        }
        return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), new ArrayList<>(e.getConstraintViolations()).get(0).getMessageTemplate());
    }

    /**
     * 打印错误日志
     *
     * @param e
     * @param request
     */
    private void packageLogMsg(RuntimeException e, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String param = HttpUtil.getParams();
        Object studentObject = session.getAttribute(UserConstant.CURRENT_STUDENT);
        String url = request.getRequestURI().substring(request.getContextPath().length());
        if (studentObject != null) {
            Student student = (Student) studentObject;
            log.error("学生[{} - {} - {}],请求 URL=[{}],操作出现系统异常,param=[{}]", student.getId(), student.getAccount(), student.getStudentName(), url, param, e);
        } else {
            log.error("学生操作出现系统异常,param=[{}], URL=[{}]", param, url, e);
        }
    }

}
