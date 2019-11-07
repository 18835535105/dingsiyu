package com.zhidejiaoyu.common.study;

import org.springframework.stereotype.Component;

import com.zhidejiaoyu.common.utils.BigDecimalUtil;

/**
 * 计算记忆强度
 *
 * @author wuchenxi
 * @date 2018年5月14日 下午4:18:41
 *
 */
@Component
public class MemoryStrengthUtil {

	/**
	 * 学生测试状态下计算记忆强度
	 *
	 * @param currentMemoryStrength
	 *            当前记忆追踪-智能记忆表中的记忆强度
	 * @param isRight
	 *            true：答对状态；false:答错状态
	 * @return
	 */
	public Double getTestMemoryStrength(Double currentMemoryStrength, Boolean isRight) {
		if (isRight) {
			double memoryStrength = BigDecimalUtil.add(currentMemoryStrength, 0.13);
			return memoryStrength > 1 ? 1.0 : memoryStrength;
		} else {
			double memoryStrength = BigDecimalUtil.div(currentMemoryStrength, 2, 2);
			return memoryStrength < 0 ? 0.0 : memoryStrength;
		}
	}

}
