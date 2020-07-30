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
    TIME_LESS_ONE_DAY(900, "TIME_LESS_ONE_DAY"),


    /**
     * 字典
     */
    DICT_EXISTED(400, "字典已经存在"),
    ERROR_CREATE_DICT(500, "创建字典失败"),
    ERROR_WRAPPER_FIELD(500, "包装字典属性失败"),
    ERROR_CODE_EMPTY(500, "字典类型不能为空"),

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),
    UPLOAD_ERROR(500, "上传图片出错"),

    /**
     * 权限和数据问题
     */
    DB_RESOURCE_NULL(400, "数据库中没有该资源"),
    NO_PERMITION(405, "权限异常"),
    REQUEST_INVALIDATE(400, "请求数据格式不正确"),
    INVALID_KAPTCHA(400, "验证码不正确"),
    CANT_DELETE_ADMIN(600, "不能删除超级管理员"),
    CANT_FREEZE_ADMIN(600, "不能冻结超级管理员"),
    CANT_CHANGE_ADMIN(600, "不能修改超级管理员角色"),

    /**
     * 账户问题
     */
    USER_ALREADY_REG(401, "该用户已经注册"),
    NO_THIS_USER(400, "没有此用户"),
    USER_NOT_EXISTED(400, "没有此用户"),
    ACCOUNT_FREEZED(401, "账号被冻结"),
    OLD_PWD_NOT_RIGHT(402, "原密码不正确"),
    TWO_PWD_NOT_MATCH(405, "两次输入密码不一致"),
    ILLEGAL_PASSWORD_LENGTH(405, "密码长度为 4 ~ 20 个字符"),

    /**
     * 错误的请求
     */
    MENU_PCODE_COINCIDENCE(400, "菜单编号和父编号不能一致"),
    EXISTED_THE_MENU(400, "请求地址已存在，不能添加"),
    DICT_MUST_BE_NUMBER(400, "字典的值必须为数字"),
    REQUEST_NULL(400, "请求有错误"),
    SESSION_TIMEOUT(400, "会话超时"),
    SERVER_ERROR(500, "服务器异常"),

    /**
     * token异常
     */
    TOKEN_EXPIRED(700, "token过期"),
    TOKEN_ERROR(700, "token验证失败"),

    /**
     * 签名异常
     */
    SIGN_ERROR(700, "签名验证失败"),

    /**
     * 其他
     */
    AUTH_REQUEST_ERROR(400, "账号密码错误"),

    INVALID_PHONE(400, "请输入正确的手机号码！"),

    /**
     * 用户操作过于频繁
     */
    REPEAT_SUBMIT(500, "正在处理，请稍后！");


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
