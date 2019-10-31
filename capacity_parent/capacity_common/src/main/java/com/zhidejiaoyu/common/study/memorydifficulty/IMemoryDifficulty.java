package com.zhidejiaoyu.common.study.memorydifficulty;

import com.zhidejiaoyu.common.pojo.StudyCapacity;

/**
 * 记忆难度计算基类
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 13:58
 */
public interface IMemoryDifficulty {
    /**
     * 获取记忆难度
     *
     * @param studyCapacity
     * @return
     */
    Integer getMemoryDifficulty(StudyCapacity studyCapacity);

    default int getMemoryDifficulty(Double memoryStrength, Integer errCount, Integer studyCount) {
        // 错误率
        double errorRate = (errCount * 1.0) / (studyCount * 1.0);

        if (memoryStrength == 1 || errorRate == 0) {
            return 0;
        } else if (errorRate == 1) {
            return 5;
        } else if (errorRate < 1 && errorRate >= 0.8) {
            return 4;
        } else if (errorRate < 0.8 && errorRate >= 0.6) {
            return 3;
        } else if (errorRate < 0.6 && errorRate >= 0.4) {
            return 2;
        } else if (errorRate < 0.4 && errorRate > 0) {
            return 1;
        }
        return 0;
    }
}
