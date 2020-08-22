package com.zhidejiaoyu.common.exception;

import com.zhidejiaoyu.common.constant.UserConstant;
import com.zhidejiaoyu.common.exception.Enum.ServiceExceptionEnum;
import com.zhidejiaoyu.common.pojo.Student;
import com.zhidejiaoyu.common.utils.http.HttpUtil;
import com.zhidejiaoyu.common.utils.server.ResponseCode;
import com.zhidejiaoyu.common.utils.server.ServerResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.ConstraintViolationException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * 异常处理
 *
 * @author 1126
 */
@Slf4j
@ControllerAdvice
public class CatchException {

    /**
     * 拦截未知异常
     *
     * @param e
     * @param request
     */
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ServerResponse<Object> unknownException(RuntimeException e, HttpServletRequest request) {
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
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    @ResponseBody
    public ServerResponse<Object> knownException(ServiceException e, HttpServletRequest request) {
        packageLogMsg(e, request);
        return ServerResponse.createByError(e.getCode(), e.getMessage());
    }

    /**
     * 拦截参数校验异常
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServerResponse<Object> constraintViolationException(ConstraintViolationException e) {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
        packageLogMsg(e, httpServletRequest);
        return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), new ArrayList<>(e.getConstraintViolations()).get(0).getMessageTemplate());
    }

    /**
     * 拦截参数校验异常
     */
    @ExceptionHandler(value = {BindException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ServerResponse<Object> bindException(Exception e) {
        HttpServletRequest httpServletRequest = HttpUtil.getHttpServletRequest();
        packageLogMsg(e, httpServletRequest);
        BindingResult bindResult = null;
        if (e instanceof BindException) {
            bindResult = ((BindException) e).getBindingResult();
        } else if (e instanceof MethodArgumentNotValidException) {
            bindResult = ((MethodArgumentNotValidException) e).getBindingResult();
        }

        if (bindResult != null && bindResult.hasErrors()) {
            return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), bindResult.getAllErrors().get(0).getDefaultMessage());

        }

        return ServerResponse.createByError(ResponseCode.ILLEGAL_ARGUMENT.getCode(), "参数异常！");
    }

    /**
     * 拦截请求超时异常
     *
     * @param e
     * @param request
     */
    @ExceptionHandler(SocketTimeoutException.class)
    @ResponseStatus(HttpStatus.REQUEST_TIMEOUT)
    @ResponseBody
    public ServerResponse<Object> requestTimeOutException(ServiceException e, HttpServletRequest request) {
        packageLogMsg(e, request);
        return ServerResponse.createByError(e.getCode(), "请求超时，请稍后重试！");
    }

    /**
     * 打印错误日志
     *
     * @param e
     * @param request
     */
    private void packageLogMsg(Exception e, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String param = HttpUtil.getParams();
        Object studentObject = session.getAttribute(UserConstant.CURRENT_STUDENT);
        String url = request.getRequestURI().substring(request.getContextPath().length());
        if (studentObject != null) {
            Student student = (Student) studentObject;
            log.error("学生[{} - {} - {}],请求 URL=[{}],操作出现系统异常,param=[{}]", student.getId(), student.getAccount(), student.getStudentName(), url, param, e);
        } else {
            log.error("操作出现系统异常,param=[{}], URL=[{}]", param, url, e);
        }
    }

}
