package com.zhidejiaoyu.student.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 测试结果页响应数据
 *
 * @author wuchenxi
 * @date 2018/7/5
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TestResultVo {
    /**
     * 奖励金币数
     */
    private Integer gold;

    /**
     * 推送课程信息
     */
    private String msg;

    /**
     * 宠物图片url
     */
    private String petUrl;

    /**
     * 宠物提示语
     */
    private String petSay;

    /**
     * 单元闯关测试响应信息中包含的状态
     * 1：当前单元指定模块都已参加过单元测试，开启当前课程的下个单元
     * 2：当前单元指定模块都已参加过单元测试，开启当前下个课程
     */
    private String lockMsg;

    public String getLockMsg() {
        return lockMsg;
    }

    public void setLockMsg(String lockMsg) {
        this.lockMsg = lockMsg;
    }

    public Integer getGold() {
        return gold;
    }

    public void setGold(Integer gold) {
        this.gold = gold;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getPetUrl() {
        return petUrl;
    }

    public void setPetUrl(String petUrl) {
        this.petUrl = petUrl;
    }

    public String getPetSay() {
        return petSay;
    }

    public void setPetSay(String petSay) {
        this.petSay = petSay;
    }

}
