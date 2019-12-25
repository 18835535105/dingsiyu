package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.dto.testbeforestudy.GradeAndUnitIdDTO;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 课程表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-11-21
 */
public interface CourseNewMapper extends BaseMapper<CourseNew> {

    /**
     * 通过单元id查询当前课程所属学段
     *
     * @param unitId
     * @return
     */
    String selectPhaseByUnitId(Long unitId);

    /**
     * 根据课程id查询学段
     *
     * @param courseId
     * @return
     */
    String selectPhaseById(@Param("courseId") Long courseId);

    /**
     * 根据单元id查询单元与年级的关系
     *
     * @param unitIds
     * @return
     */
    List<GradeAndUnitIdDTO> selectGradeByUnitIds(@Param("unitIds") List<Long> unitIds);

    String selectGradeByCourseId(@Param("courseId") Long courseId);
}
