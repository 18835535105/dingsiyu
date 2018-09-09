package com.zhidejiaoyu.common.utils.server;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

/**
 * 服务端响应工具类
 * 如果是对象的value是null 或空值，key也会消失
 *
 * @author wuchenxi
 * @date 2018年4月25日 上午11:48:15
 */
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

	@JsonIgnore
	public boolean isSuccess() {
		return this.status == ResponseCode.SUCCESS.getCode();
	}

	// 以下三个带有get方法的属性会添加到json中
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
	 * @param code
	 *            响应码
	 * @param msg
	 *            响应信息
	 * @return
	 */
	public static <T> ServerResponse<T> createBySuccess(int code, String msg) {
		return new ServerResponse<T>(code, msg);
	}

	public static <T> ServerResponse<T> createByError(int code, String msg) {
		return new ServerResponse<T>(code, msg);
	}

	// 请求成功的接口。避免了T类型data不能包括String数据的问题。
	public static <T> ServerResponse<T> createBySuccess() {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode());
	}

	public static <T> ServerResponse<T> createBySuccessMessage(String msg) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg);
	}

	public static <T> ServerResponse<T> createBySuccess(T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), data);
	}

	public static <T> ServerResponse<T> createBySuccess(String msg, T data) {
		return new ServerResponse<T>(ResponseCode.SUCCESS.getCode(), msg, data);
	}

	// 请求失败的接口
	public static <T> ServerResponse<T> createByError() {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getMsg());
	}

	public static <T> ServerResponse<T> createByErrorMessage(String errorMessage) {
		return new ServerResponse<T>(ResponseCode.ERROR.getCode(), errorMessage);
	}

	public static <T> ServerResponse<T> createByErrorCodeMessage(int errorCode, String errorMessage) {
		return new ServerResponse<T>(errorCode, errorMessage);
	}
}