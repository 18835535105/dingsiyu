package com.zhidejiaoyu.common.vo;

/**
 * 记忆追踪列表数据展示
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午1:40:19
 *
 */
public class CapacityListVo {

	/** 音标 */
	private String soundMark;

	/** 读音地址 */
	private String readUrl;

	/** 单词/例句英文 */
	private String content;

	/** 单词/例句中文 */
	private String chinese;

	/** 记忆强度 */
	private Double memeoryStrength;

	/** 距离黄金记忆点的时间 */
	private String push;

	public String getReadUrl() {
		return readUrl;
	}

	public void setReadUrl(String readUrl) {
		this.readUrl = readUrl;
	}

	public String getSoundMark() {
		return soundMark;
	}

	public void setSoundMark(String soundMark) {
		this.soundMark = soundMark;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getChinese() {
		return chinese;
	}

	public void setChinese(String chinese) {
		this.chinese = chinese;
	}

	public Double getMemeoryStrength() {
		return memeoryStrength;
	}

	public void setMemeoryStrength(Double memeoryStrength) {
		this.memeoryStrength = memeoryStrength;
	}

	public String getPush() {
		return push;
	}

	public void setPush(String push) {
		this.push = push;
	}

}
