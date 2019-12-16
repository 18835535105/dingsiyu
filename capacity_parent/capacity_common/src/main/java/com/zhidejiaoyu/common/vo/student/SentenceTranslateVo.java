package com.zhidejiaoyu.common.vo.student;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

/**
 * 例句翻译页面数据展示
 *
 * @author wuchenxi
 * @date 2018/5/21 15:00
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class SentenceTranslateVo {

    /**
     * 例句id
     */
    private Long id;

    /**
     * 例句英语原文
     */
    private String english;

    /**
     * 例句英语原文
     */
    private String sentence;

    /**
     * 课程id
     */
    private Integer courseId;

    /**
     * 单元id
     */
    private Integer unitId;

    /**
     * 例句中文
     */
    private String chinese;

    /**
     * 例句乱序
     */
    private List<String> orderEnglish;

    /**
     * 例句顺序集合，包含标点
     */
    private List<String> englishList;

    /**
     * 例句读音
     */
    private String readUrl;

    /**
     * 当前单词是学习还是复习 true：新学习单词；false：复习单词
     */
    private Boolean studyNew;

    /**
     * 单词记忆强度
     */
    private Double memoryStrength;

    /**
     * 学习进度（上次学习到第几个例句，本次接着学习）
     */
    private Long plan;

    /**
     * 是否是第一次学习例句翻译 true：第一次，需要进入学习引导页；false：不是第一次，直接进入学习页面
     */
    private Boolean firstStudy;

    /**
     * 当前单元例句的总数
     */
    private Long sentenceCount;

    /**
     * 中文乱序
     */
    @Deprecated
    List<String> orderChinese;

    /**
     * 中文翻译顺序集合，包含标点
     */
    @Deprecated
    List<String> chineseList;

    /**
     * 中文或英文乱序
     */
    private List<String> order;

    /**
     * 中文或英文正序
     */
    private List<String> list;

    /**
     * 例句或翻译正确顺序（含标点）
     */
    private List<String> rateList;
}
