package com.zhidejiaoyu.common.exceptionadvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.validation.ConstraintViolationException;

/**
 * 入参参数非法校验
 *
 * @author wuchenxi
 * @date 2018/8/14
 */
@ControllerAdvice
@Slf4j
public class ConstraintViolationExceptionAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleValidationException(ConstraintViolationException e) {
        log.error(e.getLocalizedMessage());
        return "请求参数不合法";
    }
}
