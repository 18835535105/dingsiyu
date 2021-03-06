package com.zhidejiaoyu.common.vo.testVo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 测试题返回结果，针对选择题，四个答案中有一个是正确的
 * @author wuchenxi
 * @date 2018年5月8日
 */
@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class TestResultVO implements Serializable{

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 单词或句子id
	 */
	private Long id;

	/**
	 * 试题类型
	 */
	private String type;

	/**
	 * 题目
	 */
	private String title;

	/**
	 * 试题的集合	key：答案		value：对应的key是否是正确答案
	 */
	private Map<Object, Boolean> subject = new HashMap<>();

	/**
	 * 语音url
	 */
	private String readUrl;

	/**
	 * 单元id，有些以课程为单位的题目需要返回每个单词或例句的所属单元id
	 */
	private Long unitId;

	/**
	 * 正确的单词中文翻译
	 */
	private String chinese;




}
