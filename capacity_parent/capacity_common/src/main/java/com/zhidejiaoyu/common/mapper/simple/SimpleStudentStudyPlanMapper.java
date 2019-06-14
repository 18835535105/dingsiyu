package com.zhidejiaoyu.common.mapper.simple;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.StudentStudyPlan;

import java.util.List;


/**
 * <p>
 * 学生学习计划表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-01-15
 */
public interface SimpleStudentStudyPlanMapper extends BaseMapper<StudentStudyPlan> {


    List<Long> getCourseId(Long studentId);

}
