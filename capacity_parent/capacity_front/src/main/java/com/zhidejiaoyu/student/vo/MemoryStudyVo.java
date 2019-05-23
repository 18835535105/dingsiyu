package com.zhidejiaoyu.student.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 慧记忆学习页面数据
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class MemoryStudyVo {

	/** 单词id */
	private Long wordId;

	/** 单词 */
	private String word;

	/**
	 * 音节
	 */
	private String syllable;

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
	private String imgUrl;

	/**
	 * 中文选项
	 */
	private List<Map<String, Boolean>> wordChineseList;
}
