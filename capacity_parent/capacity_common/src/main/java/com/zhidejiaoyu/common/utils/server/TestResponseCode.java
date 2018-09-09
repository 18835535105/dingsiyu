package com.zhidejiaoyu.common.utils.server;

/**
 * 测试相关状态码
 *
 * @author wuchenxi
 * @date 2018/7/13
 */
public enum  TestResponseCode {
    /**
     * 可以进行单元测试
     */
    TO_UNIT_TEST(600, "TO_UNIT_TEST"),
    /**
     * 可进行阶段测试
     */
    TO_STAGE_TEST(601, "TO_STAGE_TEST"),
    /**
     * 游戏测试已进行两次，不可再次游戏测试
     */
    GAME_TESTED_SECOND(602,"GAME_TESTED_SECOND"),
    /**
     * 已经进行过摸底测试，不可再次摸底测试
     */
    LEVEL_TESTED(603, "LEVEL_TESTED");

    private Integer code;
    private String msg;

    TestResponseCode(Integer code, String msg) {
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
