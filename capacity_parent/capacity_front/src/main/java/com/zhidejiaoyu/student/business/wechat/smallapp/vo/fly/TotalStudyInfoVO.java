package com.zhidejiaoyu.student.business.wechat.smallapp.vo.fly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 智慧飞行记录总学习记录
 *
 * @author: wuchenxi
 * @date: 2020/6/2 15:35:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TotalStudyInfoVO implements Serializable {
    private String studentName;

    /**
     * 获取的总金币数
     */
    private Integer totalGold;

    /**
     * 学生报名时间
     */
    private String firstLoginTime;

    /**
     * 总在线时长，单位秒
     */
    private Long totalOnlineTime;

    /**
     * 总有效时长，单位秒
     */
    private Long totalValidTime;

    private Integer wordCount;

    private Integer sentenceCount;

    private Integer syntaxCount;

    private Integer textCount;

    /**
     * 考题总数 测试次数还是所有答题的个数
     */
    private Integer testCount;
}
