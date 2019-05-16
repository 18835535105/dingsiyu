package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.SentenceCourse;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 课程表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
public interface SentenceCourseMapper extends BaseMapper<SentenceCourse> {

    String getVersionByUnitId(Long unitId);


    Map<String,Object> selectCourseByUnitId(Long unitId);

    List<Map<String,Object>> getAllVersion(Long studnetId);
}
