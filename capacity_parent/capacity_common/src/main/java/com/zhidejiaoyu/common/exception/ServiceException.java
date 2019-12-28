package com.zhidejiaoyu.common.exception;

import com.zhidejiaoyu.common.exception.Enum.ServiceExceptionEnum;

/**
 * 自定义异常类
 *
 * @author wuchenxi
 * @date 2019-06-28
 */
public class ServiceException extends RuntimeException {

    private Integer code;

    private String message;

    private ServiceException() {
    }

    public ServiceException(ServiceExceptionEnum serviceExceptionEnum) {
        this.code = serviceExceptionEnum.getCode();
        this.message = serviceExceptionEnum.getMessage();
    }

    public ServiceException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ServiceException(String message) {
        this.code = 500;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
