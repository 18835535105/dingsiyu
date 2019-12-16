package com.zhidejiaoyu.common.vo.simple.capacityVo;

import lombok.Data;

import java.util.Date;

/**
 * 记忆追踪列表数据展示
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午1:40:19
 */
@Data
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

	private Date pushTime;
}
