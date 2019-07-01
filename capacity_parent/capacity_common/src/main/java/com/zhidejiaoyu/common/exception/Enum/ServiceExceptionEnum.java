package com.zhidejiaoyu.common.exception.Enum;

/**
 * 异常枚举类
 *
 * @author wuchenxi
 * @date 2019-06-28
 */
public enum ServiceExceptionEnum implements BaseExceptionEnum {


    /**
     * 系统异常
     */
    EXCEPTION(500, "系统异常"),

    /**
     * 用户名或密码不能为空
     */
    NAME_OR_PASSWORD_CAN_NOT_BE_NULL(500, "用户名或密码不能为空");

    /**
     * 异常码
     */
    private Integer code;

    /**
     * 异常信息
     */
    private String message;

    ServiceExceptionEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    public void setMessage(String msg) {
        this.message = msg;
    }
}
