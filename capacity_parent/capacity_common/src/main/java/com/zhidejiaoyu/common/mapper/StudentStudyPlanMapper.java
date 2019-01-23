package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlan;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 学生学习计划表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-01-15
 */
public interface StudentStudyPlanMapper extends BaseMapper<StudentStudyPlan> {


    /**
     * 查询学生当前的学习计划
     *
     * @param studentId
     * @param startUnit
     * @param endUnit
     * @param type
     * @return
     */
    StudentStudyPlan selectCurrentPlan(@Param("studentId") Long studentId, @Param("startUnit") Long startUnit, @Param("endUnit") Long endUnit, @Param("type") int type);

    /**
     * 查找下一个学习计划
     *
     *
     * @param studentId
     * @param planId
     * @param type
     * @return
     */
    StudentStudyPlan selectNextPlan(@Param("studentId") Long studentId, @Param("planId") Integer planId, @Param("type") int type);
}
