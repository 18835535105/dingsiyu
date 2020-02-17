package com.zhidejiaoyu.student.business.smallapp.enums;

/**
 * 授权响应码枚举
 *
 * @author: wuchenxi
 * @date: 2020/2/17 09:56:56
 */
public enum AuthorizationEnum {

    SYS_BUSY(-1, "系统繁忙"),
    SUCCESS(0, "授权成功"),
    CODE_INVALID(40029, "code无效"),
    REQUEST_IS_FREQUENT(45011, "请求过于频繁");

    private Integer code;
    private String msg;

    AuthorizationEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static String getMsg(int code) {
        for (AuthorizationEnum value : AuthorizationEnum.values()) {
            if (value.getCode() == code) {
                return value.getMsg();
            }
        }
        return "";
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    @Override
    public String toString() {
        return "AuthorizationEnum{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }
}
