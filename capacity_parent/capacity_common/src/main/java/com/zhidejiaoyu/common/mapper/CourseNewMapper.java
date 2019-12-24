package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.CourseNew;
import org.apache.ibatis.annotations.Param;

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
     * 根据课程id查询学段
     * @param courseId
     * @return
     */
    String selectPhseById(@Param("courseId") Long courseId);
}
