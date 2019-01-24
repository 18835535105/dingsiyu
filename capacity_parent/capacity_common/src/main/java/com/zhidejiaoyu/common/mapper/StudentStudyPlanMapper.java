package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlan;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 学生学习计划表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-01-15
 */
public interface StudentStudyPlanMapper extends BaseMapper<StudentStudyPlan> {


    List<Map<String,Object>> selByStudentId(@Param("studentId") Long studentId,@Param("type") int type);

    List<StudentStudyPlan> selByStudentIdAndCourseId(@Param("studentId") Long studentId,@Param("courseId") Long courseId);
}
