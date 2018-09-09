package com.zhidejiaoyu.student.vo;

/**
 * 单词本、句子本信息摘要（顶部信息）
 * 
 * @author wuchenxi
 * @date 2018年5月19日 下午5:57:12
 *
 */
public class BookInfoVo {
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

	public Long getTotal() {
		return total;
	}

	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getNotKnow() {
		return notKnow;
	}

	public void setNotKnow(Long notKnow) {
		this.notKnow = notKnow;
	}

	public Long getKnow() {
		return know;
	}

	public void setKnow(Long know) {
		this.know = know;
	}

	public Long getResidue() {
		return residue;
	}

	public void setResidue(Long residue) {
		this.residue = residue;
	}

	public Double getScale() {
		return scale;
	}

	public void setScale(Double scale) {
		this.scale = scale;
	}

	public Double getPlan() {
		return plan;
	}

	public void setPlan(Double plan) {
		this.plan = plan;
	}
}
