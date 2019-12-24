package com.zhidejiaoyu.common.mapper;

import com.zhidejiaoyu.common.pojo.SyntaxUnit;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 单元表 Mapper 接口
 * </p>
 *
 * @author zdjy
 * @since 2019-10-29
 */
public interface SyntaxUnitMapper extends BaseMapper<SyntaxUnit> {

    /**
     * 根据课程id获取所有单元
     *
     * @param courseId
     * @return
     */
    List<Long> selectUnitIdByCourseId(@Param("courseId") Long courseId);

    /**
     * 查询当前课程最大单元id
     *
     * @param unitId
     * @return
     */
    Long selectMaxUnitIdByUnitId(@Param("unitId") Long unitId);

    /**
     * 查询当前单元的课程id
     *
     * @param unitId
     * @return
     */
    Long selectCourseIdByUnitId(@Param("unitId") Long unitId);
}