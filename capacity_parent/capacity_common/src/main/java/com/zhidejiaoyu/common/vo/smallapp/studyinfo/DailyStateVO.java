package com.zhidejiaoyu.common.vo.smallapp.studyinfo;

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

    /**
     * 是否已打卡
     */
    private Boolean card;

    private String petName;
}
