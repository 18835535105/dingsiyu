package com.zhidejiaoyu.common.dto;

import lombok.Data;

/**
 * @author wuchenxi
 * @date 2019-07-11
 */
@Data
public class EndValidTimeDto {

    /**
     * 学习模块(有效时长)，区分各个学习模块的时长，7：单元闯关测试；8：复习测试；9：已学测试；10：熟词测试；11：生词测试；
     *                       12：五维测试；13：任务课程；'14:单词辨音; 15:词组辨音; 16:单词认读; 17:词组认读; 18:词汇考点; 19:句型认读;
     *                       20:语法辨析; 21单词拼写; 22:词组拼写;
     */
    Integer classify;
    Long courseId;
    Long unitId;

    /**
     * 有效时长
     */
    String validTime;

    Long valid;

    String num;

    /**
     * 在线时长，不作为接口参数，在逻辑内封装的字段
     */
    Long onlineTime;
}
