package com.zhidejiaoyu.student.business.wechat.smallapp.vo.fly;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 智慧飞行记录指定序号学习记录
 *
 * @author: wuchenxi
 * @date: 2020/6/2 15:35:35
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudyInfoVO implements Serializable {

    private String studentName;

    /**
     * 日期， yyyy-MM-dd
     */
    private String date;

    /**
     * 当天获取的总金币数
     */
    private Integer totalGold;

    /**
     * 当天总在线时长，单位秒
     */
    private Integer totalOnlineTime;

    /**
     * 当天总有效时长，单位秒
     */
    private Integer totalValidTime;

    /**
     * 学习内容， 模块-单元
     */
    private List<String> contents;

    private List<Map<String,String>> errorWord;

    private List<Map<String,String>> errorSentence;

    private List<String> errorSyntax;

    private List<String> errorText;

    /**
     * 考题总数 测试次数还是所有答题的个数
     */
    private List<Map<String,String>> errorTest;

    /**
     * 老师对学生的评价
     */
    private String evaluate;

    /**
     * 学生表现（1-5颗星）
     */
    private Integer show;

    /**
     * 备注
     */
    private String comment;

    /**
     * 座位号（在家填写0）
     */
    private Integer siteNo;
}
