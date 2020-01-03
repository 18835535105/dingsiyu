package com.zhidejiaoyu.common.study.memorystrength;

import com.zhidejiaoyu.common.utils.BigDecimalUtil;

/**
 * @author: wuchenxi
 * @Date: 2019/10/30 17:19
 */
public interface IMemoryStrength {

    /**
     * 学生学习状态下计算记忆强度
     *
     * @param currentMemoryStrength 当前记忆追踪-智能记忆表中的记忆强度
     * @param isRight               true：答对状态；false:答错状态
     * @return
     */
    Double getMemoryStrength(Double currentMemoryStrength, Boolean isRight);

    /**
     * 计算记忆强度
     *
     * @param currentMemoryStrength
     * @param isRight
     * @return
     */
    default Double defaultMemoryStrength(Double currentMemoryStrength, Boolean isRight) {
        if (isRight) {
            double memoryStrength = BigDecimalUtil.add(currentMemoryStrength, 0.13);
            return memoryStrength > 1 ? 1.0 : memoryStrength;
        }
        double memoryStrength = BigDecimalUtil.sub(currentMemoryStrength, 0.15);
        return memoryStrength < 0 ? 0.0 : memoryStrength;
    }
}
