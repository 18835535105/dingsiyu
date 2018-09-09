package com.zhidejiaoyu.common.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * 记忆追踪
 * 
 * @author qizhentao
 * @version 1.0
 */
@Data
public class CapacityReview implements Serializable {
	/** 记忆追踪id */
	private Long id;
	/** 学生id */
	private Long student_id;
	/** 课程id */
	private Long course_id;
	/** 单元id */
	private Long unit_id;
	/** 单词id */
	private Long vocabulary_id;
	/** 单词 */
	private String word;
	/** 单词中文意思 */
	private String word_chinese;
	/** 答错次数 */
	private int fault_time;
	/** 黄金记忆点时间 */
	private String push;
	/** 记忆强度 */
	private Double memory_strength;
	/** 分类 1=慧记忆 2=听写 3=默写 4=例句听写 5=例句翻译 6=例句默写*/
	private String classify;
	
	private String syllable;
}
