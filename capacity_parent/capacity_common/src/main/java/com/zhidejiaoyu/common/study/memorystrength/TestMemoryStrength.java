package com.zhidejiaoyu.common.study.memorystrength;

import com.zhidejiaoyu.common.utils.BigDecimalUtil;

/**
 * 测试逻辑获取记忆强度
 *
 * @author: wuchenxi
 * @Date: 2019/10/30 17:26
 */
public class TestMemoryStrength implements IMemoryStrength {
    @Override
    public Double getMemoryStrength(Double currentMemoryStrength, Boolean isRight) {
        if (isRight) {
            double memoryStrength = BigDecimalUtil.add(currentMemoryStrength, 0.13);
            return memoryStrength > 1 ? 1.0 : memoryStrength;
        }
        double memoryStrength = BigDecimalUtil.div(currentMemoryStrength, 2, 2);
        return memoryStrength < 0 ? 0.0 : memoryStrength;
    }
}
