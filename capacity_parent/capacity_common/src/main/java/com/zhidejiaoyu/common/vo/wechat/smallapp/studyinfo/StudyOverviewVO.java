package com.zhidejiaoyu.common.vo.wechat.smallapp.studyinfo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 飞行记录学习总览
 *
 * @author: wuchenxi
 * @date: 2020/7/1 10:46:46
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyOverviewVO implements Serializable {
    /**
     * 总学习时长
     */
    private Long totalOnlineTime;

    /**
     * 总有效时长
     */
    private Long totalValidTime;

    /**
     * 掌握单词数
     */
    private Integer wordCount;

    /**
     * 学习次数，学生登录次数
     */
    private Integer studyCount;
}
