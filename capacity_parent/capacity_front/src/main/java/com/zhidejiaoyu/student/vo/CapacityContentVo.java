package com.zhidejiaoyu.student.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 记忆追踪中鼠标悬浮到指定单词或例句上时，页面展示的该单词或者例句的详细学习状况
 *
 * @author wuchenxi
 * @date 2018年5月19日 上午9:30:38
 */
@Data
public class CapacityContentVo implements Serializable {

	/** 单词/例句翻译 */
	private String chinese;

	/** 学习次数 */
	private Integer studyCount;

	/** 答错次数 */
	private Integer faultCount;

	/** 记忆强度 */
	private Double memoryStrength;

	/** 距离黄金记忆点时间 */
	private String push;

	/** 读音地址 */
	private String readUrl;

}
