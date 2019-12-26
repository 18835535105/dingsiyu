package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlanNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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

    /**
     * 获取最终优先级最高的 limit 条记录
     *
     * @param studentId
     * @return
     */
    List<StudentStudyPlanNew> selectMaxFinalLevelByLimit(@Param("studentId") Long studentId, @Param("limit") Integer limit);

    int selectByStudentIdAndUnitId(@Param("studentId") Long studentId, @Param("unitId") Long unitId);
}
