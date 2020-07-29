package com.dfdz.teacher.exception;

import com.zhidejiaoyu.common.exception.Enum.ServiceExceptionEnum;

/**
 * 封装guns的异常
 *
 * @author zdjy
 * @Date 2017/12/28 下午10:32
 */
public class GunsException extends RuntimeException {

    private Integer code;

    private String message;

    public GunsException(ServiceExceptionEnum serviceExceptionEnum) {
        this.code = serviceExceptionEnum.getCode();
        this.message = serviceExceptionEnum.getMessage();
    }

    public GunsException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public GunsException(String message) {
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
