package com.zhidejiaoyu.common.utils.server;

/**
 * 测试相关状态码
 *
 * @author wuchenxi
 * @date 2018/7/13
 */
public enum GoldResponseCode {
    /**
     * 金币不足
     */
    LESS_GOLD(800, "GOLD_ERROR"),
    /**
     * 需要扣除金币，让用户进行确认是否扣除
     */
    NEED_REDUCE_GOLD(801, "NEED_REDUCE_GOLD");

    private Integer code;
    private String msg;

    GoldResponseCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
