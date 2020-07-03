package com.zhidejiaoyu.common.utils.server;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 服务端响应工具类
 * 如果是对象的value是null 或空值，key也会消失
 *
 * @author wuchenxi
 * @date 2018年4月25日 上午11:48:15
 */
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ServerResponse<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.data = data;
        this.status = status;
        this.msg = msg;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    /**
     * 以下三个带有get方法的属性会添加到json中
     *
     * @return
     */
    public int getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    /**
     * 自定义响应码及响应信息
     *
     * @param status 响应码
     * @param msg  响应信息
     * @return
     */
    public static <T> ServerResponse<T> createBySuccess(int status, String msg) {
        return new ServerResponse<>(status, msg);
    }

    public static <T> ServerResponse<T> createByError(int code, String msg) {
        return new ServerResponse<>(code, msg);
    }

    public static <T> ServerResponse<T> createByError(ResponseCode responseCode) {
        return new ServerResponse<>(responseCode.getCode(), responseCode.getMsg());
    }

    /**
     * 请求成功的接口。避免了T类型data不能包括String数据的问题。
     */
    public static <T> ServerResponse<T> createBySuccess() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> createBySuccess(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> createBySuccess(ResponseCode code) {
        return new ServerResponse<>(code.getCode(), code.getMsg());
    }

    public static <T> ServerResponse<T> createBySuccess(int status, T data) {
        return new ServerResponse<>(status, data);
    }

    /**
     * 请求失败的接口
     *
     * @param <T>
     * @return
     */
    public static <T> ServerResponse<T> createByError() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg());
    }

    public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
        return new ServerResponse<>(errorCode, errorMessage);
    }
}
