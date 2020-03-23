package com.zhidejiaoyu.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zhidejiaoyu.common.pojo.TeksUnit;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 单元表 Mapper 接口
 * </p>
 *
 * @author stylefeng
 * @since 2019-03-29
 */
public interface TeksUnitMapper extends BaseMapper<TeksUnit> {

    /**
     * 根据单元id获取所属课程id
     *
     * @param unitId
     * @return
     */
    @Select("select course_id from teks_unit where id = #{unitId}")
    Long selectCourseIdByUnitId(Long unitId);

    List<Map<String, Object>> selectByStudentIdAndCourseIdAndStartUnitIdAndEndUnitId(@Param("courseId") Long courseId, @Param("startUnitId") Long startUnitId, @Param("endUnitId") Long endUnitId, @Param("studentId") Long studentId);

}
