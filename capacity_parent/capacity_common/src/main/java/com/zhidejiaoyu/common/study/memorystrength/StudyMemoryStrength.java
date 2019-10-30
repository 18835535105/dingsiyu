package com.zhidejiaoyu.common.study.memorystrength;

/**
 * 学习过程中计算记忆强度
 *
 * @author: wuchenxi
 * @Date: 2019/10/30 17:24
 */
public class StudyMemoryStrength implements IMemoryStrength {
    @Override
    public Double getMemoryStrength(Double currentMemoryStrength, Boolean isRight) {
        return defaultetMemoryStrength(currentMemoryStrength, isRight);
    }
}
