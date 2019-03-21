package com.zhidejiaoyu.student.vo;

import lombok.Data;

/**
 * 慧默写学习数据vo
 * 
 * @author wuchenxi
 * @date 2018年5月17日 下午4:46:43
 *
 */
@Data
public class WordWriteStudyVo {

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
	private String soundmark;

	/** 当前单词是学习还是复习 true：新学习单词；false：复习单词 */
	private Boolean studyNew;

	/** 单词记忆强度 */
	private Double memoryStrength;

	/** 学习进度（上次学习到第几个单词，本次接着学习） */
	private Long plan;

	/** 是否是第一次学习慧记忆 true：第一次，需要进入学习引导页；false：不是第一次，直接进入学习页面 */
	private Boolean firstStudy;

	/** 当前单元单词的总数 */
	private Long wordCount;

	/**
	 * 单词读音地址
	 */
	private String readUrl;

}
