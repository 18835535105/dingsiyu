package com.zhidejiaoyu.common.Vo.read;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.zhidejiaoyu.common.Vo.study.MemoryStudyVo;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 开始强化数据 vo
 *
 * @author wuchenxi
 * @date 2019-08-14 14:49
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class StrengthenVo implements Serializable {

    /**
     * 单词 id
     */
    private Long id;

    /** 单词 */
    private String word;

    /**
     * 音节
     */
    private String wordyj;

    /** 单词中文意思含词性 */
    private String wordChinese;

    /** 单词音标 */
    private String soundMark;

    /** 单词记忆难度 */
    private Integer memoryDifficulty;

    /** 当前单词是学习还是复习 true：新学习单词；false：复习单词 */
    private Boolean studyNew;

    /** 单词记忆强度 */
    private Double memoryStrength;

    /** 学习进度（上次学习到第几个单词，本次接着学习） */
    private Long plan;

    /** 是否是第一次学习慧记忆 true：第一次，需要进入学习引导页；false：不是第一次，直接进入学习页面*/
    private Boolean firstStudy;

    /** 当前单元单词的总数 */
    private Long wordCount;

    private String readUrl;

    /**
     * 认知引擎
     */
    private Integer engine;

    /**
     * 单词图片地址
     */
    private String recordpicurl;

    /**
     * 单词图鉴需要该字段（无意义，值为 2）
     */
    private Integer type = 2;

    /**
     * 中文选项
     */
    private List<Map<String, Boolean>> wordChineseList;

    /**
     * 单词图鉴图片下面的数据
     */
    private Map<String, Boolean> subject;
}
