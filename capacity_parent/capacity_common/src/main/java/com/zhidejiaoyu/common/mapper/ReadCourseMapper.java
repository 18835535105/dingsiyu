package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.ReadCourse;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 阅读课程表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-07-15
 */
public interface ReadCourseMapper extends BaseMapper<ReadCourse> {


    /**
     * 根据起始单元和结束单元获取课程信息
     * @param startUnitId
     * @param endUnitId
     * @return
     */
    List<ReadCourse> selCourseByStartUnitAndEndUnit(@Param("startUnitId") Long startUnitId,@Param("endUnitId") Long endUnitId);

    /**
     * 根据起始排序和结束排序获取排序信息
     * @param startUnitId
     * @param endUnitId
     * @param grade
     * @return
     */
    List<Long> selReadSortByStartReadSortAndEndReadSort(@Param("startUnitId") Long startUnitId,@Param("endUnitId") Long endUnitId,@Param("grade")String grade);

    /**
     * 根据年级，排序集合获取课程信息
     * @param grade
     * @param unitList
     * @return
     */
    List<Map<String,Object>> selSort(@Param("grade") String grade,@Param("unitList") List<Long> unitList);

    /**
     * 根据月份排序与年级获取课程信息
     * @param sort
     * @param grade
     * @return
     */
    List<Long> selBySortAndGrade(@Param("sort") Long sort,@Param("grade") String grade);
}
