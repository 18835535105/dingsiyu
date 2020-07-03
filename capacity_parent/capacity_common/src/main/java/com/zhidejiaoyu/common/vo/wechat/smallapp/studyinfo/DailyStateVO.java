package com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo;

import lombok.*;

/**
 * @author: wuchenxi
 * @date: 2020/4/7 18:02:02
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class DailyStateVO extends DurationInfoVO {

    private Long studentId;

    private String account;

    private String studentName;

    private String msg;

    private String msg1;

    /**
     * 学段
     */
    private String phase;

    /**
     * 学习进度，本周在现时长/4小时
     */
    private String studyProgress;

    /**
     * 连续签到天数
     */
    private Integer cardDays;
}
