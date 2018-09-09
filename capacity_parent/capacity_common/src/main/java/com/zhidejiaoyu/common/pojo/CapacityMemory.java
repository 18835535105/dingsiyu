package com.zhidejiaoyu.common.pojo;

import java.io.Serializable;
import java.util.Date;

/**
 * 记忆追踪-慧记忆表
 * 
 * @author wuchenxi
 * @date 2018年5月10日
 */
public class CapacityMemory implements Serializable {
	public Long id;

	public Long studentId;

	public Long courseId;

	public Long unitId;

	public Long vocabularyId;

	public String word;

	public String syllable;

	public String wordChinese;

	/** 答错次数 */
	public Integer faultTime;

	/** 黄金记忆点时间 */
	public Date push;
	
	/** 记忆强度 */
	public Double memoryStrength;

	public String getSyllable() {
		return syllable;
	}

	public void setSyllable(String syllable) {
		this.syllable = syllable;
	}

	public Double getMemoryStrength() {
		return memoryStrength;
	}

	public void setMemoryStrength(Double memoryStrength) {
		this.memoryStrength = memoryStrength;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getStudentId() {
		return studentId;
	}

	public void setStudentId(Long studentId) {
		this.studentId = studentId;
	}

	public Long getCourseId() {
		return courseId;
	}

	public void setCourseId(Long courseId) {
		this.courseId = courseId;
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

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word == null ? null : word.trim();
	}

	public String getWordChinese() {
		return wordChinese;
	}

	public void setWordChinese(String wordChinese) {
		this.wordChinese = wordChinese == null ? null : wordChinese.trim();
	}

	public Integer getFaultTime() {
		return faultTime;
	}

	public void setFaultTime(Integer faultTime) {
		this.faultTime = faultTime;
	}

	public Date getPush() {
		return push;
	}

	public void setPush(Date push) {
		this.push = push;
	}
}