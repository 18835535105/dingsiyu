package com.zhidejiaoyu.common.utils.simple.testUtil;

import java.io.Serializable;
import java.util.List;

/**
 * 例句测试题返回结果
 *
 * @author wuchenxi
 * @date 2018年5月17日 上午11:35:55
 *
 */
public class SimpleSentenceTestResult implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/** 例句id */
	private Long id;

	/** 正常例句英文 */
	private String sentence;

	/** 乱序英文例句 */
	private List<String> chaosSentence;

	/** 例句中文 */
	private String sentenctChinese;

	/** 例句读音地址 */
	private String readUrl;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSentence() {
		return sentence;
	}

	public void setSentence(String sentence) {
		this.sentence = sentence;
	}

	public List<String> getChaosSentence() {
		return chaosSentence;
	}

	public void setChaosSentence(List<String> chaosSentence) {
		this.chaosSentence = chaosSentence;
	}

	public String getSentenctChinese() {
		return sentenctChinese;
	}

	public void setSentenctChinese(String sentenctChinese) {
		this.sentenctChinese = sentenctChinese;
	}

	public String getReadUrl() {
		return readUrl;
	}

	public void setReadUrl(String readUrl) {
		this.readUrl = readUrl;
	}
}
