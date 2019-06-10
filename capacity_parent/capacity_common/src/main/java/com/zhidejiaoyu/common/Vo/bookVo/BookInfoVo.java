package com.zhidejiaoyu.common.Vo.bookVo;

import lombok.Data;

import java.io.Serializable;

/**
 * 单词本、句子本信息摘要（顶部信息）
 *
 * @author wuchenxi
 * @date 2018年5月19日 下午5:57:12
 *
 */
@Data
public class BookInfoVo implements Serializable {
	/** 总词/句数 */
	private Long total;

	/** 生词/句数 */
	private Long notKnow;

	/** 熟词/句数 */
	private Long know;

	/** 剩余数 */
	private Long residue;

	/** 生词比 */
	private Double scale;

	/** 进度 */
	private Double plan;
}
