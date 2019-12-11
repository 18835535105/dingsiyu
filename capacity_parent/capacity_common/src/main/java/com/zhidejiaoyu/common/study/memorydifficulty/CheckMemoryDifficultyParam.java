package com.zhidejiaoyu.common.study.memorydifficulty;

import com.zhidejiaoyu.common.pojo.StudyCapacity;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 校验获取记忆难度方法入参
 *
 * @author: wuchenxi
 * @Date: 2019/10/31 14:05
 */
@Slf4j
public class CheckMemoryDifficultyParam {

    /**
     * 验证必须参数
     *
     * @param studyCapacity
     */
    boolean checkParam(StudyCapacity studyCapacity) {
        if (Objects.isNull(studyCapacity)) {
            log.error("校验获取记忆难度方法参数时，studyCapacity=null");
            return false;
        }
        if (Objects.isNull(studyCapacity.getUnitId())) {
            log.error("校验获取记忆难度方法参数时，unitId=null");
            return false;
        }
        if (Objects.isNull(studyCapacity.getStudentId())) {
            log.error("校验获取记忆难度方法参数时，studentId=null");
            return false;
        }
        if (Objects.isNull(studyCapacity.getWordId())) {
            log.error("校验获取记忆难度方法参数时，wordId=null");
            return false;
        }
        if (Objects.isNull(studyCapacity.getMemoryStrength())) {
            log.error("校验获取记忆难度方法参数时，memoryStrength=null");
            return false;
        }
        if (Objects.isNull(studyCapacity.getFaultTime())) {
            log.error("校验获取记忆难度方法参数时，faultTime=null");
            return false;
        }
        return true;
    }
}
