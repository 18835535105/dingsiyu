package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;

public class UnitVocabulary implements Serializable {
	private Long unitId;

	private Long vocabularyId;

	/** 词汇分类，1：重点词汇(默认)；2：课标词汇 */
	private Integer classify;
	
	/** 拼接 用于删除关联表 */
	private String str;

	private String wordChinese;

	public String getWordChinese() {
		return wordChinese;
	}

	public void setWordChinese(String wordChinese) {
		this.wordChinese = wordChinese;
	}

	public String getStr() {
		return str;
	}

	public void setStr(String str) {
		this.str = str;
	}

	public Long getUnitId() {
		return unitId;
	}

	public void setUnitId(Long unitId) {
		this.unitId = unitId;
	}

	public Long getVocabularyId() {
		return vocabularyId;
	}

	public void setVocabularyId(Long vocabularyId) {
		this.vocabularyId = vocabularyId;
	}

	public Integer getClassify() {
		return classify;
	}

	public void setClassify(Integer classify) {
		this.classify = classify;
	}
	
	
}