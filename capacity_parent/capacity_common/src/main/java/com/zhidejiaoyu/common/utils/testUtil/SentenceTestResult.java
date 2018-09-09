package com.zhidejiaoyu.common.utils.testUtil;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 例句测试题返回结果
 * 
 * @author wuchenxi
 * @date 2018年5月17日 上午11:35:55
 *
 */
@Data
public class SentenceTestResult implements Serializable {

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
	private String sentenceChinese;

	/** 例句读音地址 */
	private String readUrl;

}
