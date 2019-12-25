package com.zhidejiaoyu.common.mapper;

import com.sun.tracing.dtrace.ProviderAttributes;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 学生学习计划表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-12-19
 */
public interface StudentStudyPlanNewMapper extends BaseMapper<StudentStudyPlanNew> {

    /**
     * 查看数据
     *
     * @param studentId
     * @param unitId
     * @param easyOrHard
     * @return
     */
    StudentStudyPlanNew selectByStudentIdAndUnitIdAndEasyOrHard
    (@Param("studentId") Long studentId, @Param("unitId") Long unitId, @Param("easyOrHard") int easyOrHard);

    int selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);
}
