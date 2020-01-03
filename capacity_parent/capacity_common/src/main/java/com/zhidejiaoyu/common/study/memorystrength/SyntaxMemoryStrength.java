package com.zhidejiaoyu.common.study.memorystrength;

import org.springframework.stereotype.Component;

/**
 * 超级语法模块计算记忆强度
 * 初始值为0.38
 *
 * @author: wuchenxi
 * @Date: 2019/10/30 17:27
 */
@Component
public class SyntaxMemoryStrength implements IMemoryStrength {
    @Override
    public Double getMemoryStrength(Double currentMemoryStrength, Boolean isRight) {
        return defaultMemoryStrength(currentMemoryStrength, isRight);
    }
}
