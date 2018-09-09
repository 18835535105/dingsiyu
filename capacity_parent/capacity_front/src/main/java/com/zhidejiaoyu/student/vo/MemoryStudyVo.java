package com.zhidejiaoyu.student.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 慧记忆学习页面数据
 * 
 * @author wuchenxi
 * @date 2018年5月10日
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)public class MemoryStudyVo{

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

	public String getSyllable() {
		return syllable;
	}

	public void setSyllable(String syllable) {
		this.syllable = syllable;
	}

	public String getReadUrl() {
		return readUrl;
	}

	public void setReadUrl(String readUrl) {
		this.readUrl = readUrl;
	}

	public Long getWordId() {
		return wordId;
	}

	public void setWordId(Long wordId) {
		this.wordId = wordId;
	}

	public Long getWordCount() {
		return wordCount;
	}

	public void setWordCount(Long wordCount) {
		this.wordCount = wordCount;
	}

	public Long getPlan() {
		return plan;
	}

	public void setPlan(Long plan) {
		this.plan = plan;
	}

	public Boolean getStudyNew() {
		return studyNew;
	}

	public void setStudyNew(Boolean studyNew) {
		this.studyNew = studyNew;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public String getWordChinese() {
		return wordChinese;
	}

	public void setWordChinese(String wordChinese) {
		this.wordChinese = wordChinese;
	}

    public String getSoundMark() {
        return soundMark;
    }

    public void setSoundMark(String soundMark) {
        this.soundMark = soundMark;
    }

    public Boolean getFirstStudy() {
        return firstStudy;
    }

    public void setFirstStudy(Boolean firstStudy) {
        this.firstStudy = firstStudy;
    }

    public Integer getMemoryDifficulty() {
		return memoryDifficulty;
	}

	public void setMemoryDifficulty(Integer memoryDifficulty) {
		this.memoryDifficulty = memoryDifficulty;
	}

	public Double getMemoryStrength() {
		return memoryStrength;
	}

	public void setMemoryStrength(Double memoryStrength) {
		this.memoryStrength = memoryStrength;
	}

}
