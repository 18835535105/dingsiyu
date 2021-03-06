package com.zhidejiaoyu.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 测试结果页响应数据
 *
 * @author wuchenxi
 * @date 2018/7/5
 */
@Data
public class TestResultVo implements Serializable {
    /**
     * 奖励金币数
     */
    private Integer gold;

    /**
     * 测试结果页弹框显示的提示语
     */
    private String msg;

    /**
     * 测试结果页背景上的提示语
     */
    private String backMsg;

    /**
     * 宠物图片url
     */
    private String petUrl;

    /**
     * 宠物提示语
     */
    private String petSay;

    /**
     * 奖励能量数
     */
    private Integer energy;

    /**
     * 单元闯关测试响应信息中包含的状态
     * 1：当前单元指定模块都已参加过单元测试，开启当前课程的下个单元
     * 2：当前单元指定模块都已参加过单元测试，开启当前下个课程
     */
    private String lockMsg;

    /**
     * 测试id
     */
    private Long testId;

    /**
     * 分数
     */
    private Integer point;

    /**
     * 绝招好课测试，奖励代金券、飞船等信息说明文字
     */
    private AwardInfo awardInfo;

    /**
     * 绝招好课测试，代金券、飞船等信息
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AwardInfo {
        /**
         * 奖励代金券个数
         */
        private Integer voucher;

        /**
         * 奖励飞船信息
         */
        List<Map<String, String>> equipmentList;
    }
}
