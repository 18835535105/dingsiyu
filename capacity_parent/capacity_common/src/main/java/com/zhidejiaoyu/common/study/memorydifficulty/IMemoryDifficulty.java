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

    /**
     * 获取记忆难度
     *
     * @param memoryStrength 记忆强度
     * @param errCount       错误次数
     * @param studyCount     学习次数
     * @return
     */
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

    /**
     * 匹配studyModel
     *
     * @param type
     * @return
     */
    default String getStudyModel(int type) {
        String studyModel;
        switch (type) {
            case 1:
                studyModel = "单词图鉴";
                break;
            case 2:
                studyModel = "单词播放机";
                break;
            case 3:
                studyModel = "慧记忆";
                break;
            case 4:
                studyModel = "慧听写";
                break;
            case 5:
                studyModel = "慧默写";
                break;
            case 6:
                studyModel = "单词游戏";
                break;
            case 7:
                studyModel = "句型翻译";
                break;
            case 8:
                studyModel = "句型听力";
                break;
            case 9:
                studyModel = "音译练习";
                break;
            case 10:
                studyModel = "句型默写";
                break;
            case 11:
                studyModel = "课文试听";
                break;
            case 12:
                studyModel = "课文训练";
                break;
            case 13:
                studyModel = "闯关测试";
                break;
            case 14:
                studyModel = "课文跟读";
                break;
            case 15:
                studyModel = "读语法";
                break;
            case 17:
                studyModel = "写语法";
                break;
            case 18:
                studyModel = "语法游戏";
                break;
            default:
                studyModel = null;
        }
        return studyModel;
    }
}
