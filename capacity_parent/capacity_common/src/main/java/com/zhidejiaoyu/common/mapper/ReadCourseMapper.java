package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadCourse;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 阅读课程表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadCourseMapper extends BaseMapper<ReadCourse> {


    List<ReadCourse> selCourseByStartUnitAndEndUnit(@Param("startUnitId") Long startUnitId,@Param("endUnitId") Long endUnitId);


}
