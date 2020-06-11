package com.zhidejiaoyu.common.utils.server;

/**
 * 服务器响应码
 *
 * @author wuchenxi
 * @date 2018年4月25日 下午1:48:53
 */
public enum ResponseCode {
    /**
     * 响应成功
     */
    SUCCESS(200, "SUCCESS"),

    /**
     * 原密码输入错误
     */
    PASSWORD_ERROR(300, "PASSWORD_ERROR"),

    /**
     * 参数非法
     */
    ILLEGAL_ARGUMENT(400, "ILLEGAL_ARGUMENT"),

    /**
     * 未查询到数据
     */
    NO_DATA(400, "未查询到数据！"),

    /**
     * 无权限访问
     */
    FORBIDDEN(403, "FORBIDDEN"),

    /**
     * 未绑定微信小程序、微信公众号、企业微信等
     */
    NO_BIND(407, "NO_BIND"),

    /**
     * 响应失败
     */
    ERROR(500, "FAIL"),

    /**
     * 本单元已学完
     */
    UNIT_FINISH(600, "UNIT_FINISH"),

    /**
     * 本课程学习完毕
     */
    COURSE_FINISH(601, "COURSE_FINISH"),

    /**
     * 文本中含有敏感词
     */
    SENSITIVE_WORD(700, "SENSITIVE_WORD"),

    /**
     * 上个流程没有完成
     */
    PREVIOUS_FLOW_UN_OVER(800, "PREVIOUS_FLOW_UN_OVER"),

    /**
     * 时间不足24小时
     */
    TIME_LESS_ONE_DAY(900, "TIME_LESS_ONE_DAY");


    private Integer code;
    private String msg;

    ResponseCode(Integer code, String msg) {
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
