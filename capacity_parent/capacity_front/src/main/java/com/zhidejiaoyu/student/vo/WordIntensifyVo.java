package com.zhidejiaoyu.student.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 慧记忆词义强化页面数据
 *
 * @author wuchenxi
 * @date 2018年5月10日
 */
@Data
public class WordIntensifyVo implements Serializable {

	/** 单词id */
	private Long wordId;

	/** 单词 */
	private String word;

	/** 单词中文意思含词性 */
	private String wordChinese;

	/** 单词音标 */
    private String soundMark;

	/** 单词记忆强度 */
	private Double memoryStrength;

	/** 单词读音地址 */
	private String readUrl;

}
