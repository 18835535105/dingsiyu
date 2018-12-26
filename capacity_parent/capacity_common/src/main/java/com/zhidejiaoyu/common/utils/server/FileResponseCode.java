package com.zhidejiaoyu.common.utils.server;

/**
 * 文件相关响应数据
 *
 * @author wuchenxi
 * @date 2018-12-25
 */
public enum FileResponseCode {
    /**
     * 文件过大
     */
    TOO_LARGE(421, "FILE_TOO_LARGE");

    private int code;
    private String msg;

    FileResponseCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
